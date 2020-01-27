package com.ernestas.familyfeudbot;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.coyote.Response;
import org.apache.logging.log4j.message.Message;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class QuestionService {

  private String secretKey;

  @Value("${slack.secret.key}")
  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  private ArrayList<Question> questionList = new ArrayList<>();

  private Question currentQuestion;
  private Instant currentTime;

  private Random random = new Random();

  private final String FAMILY_FEUD_CHANNEL = "CSSUB5Z8U";

  private RestTemplate restTemplate = new RestTemplate();


  @PostConstruct
  public void loadQuestions() {
    try {

      Resource resource = new ClassPathResource("questions.xlsx");
      InputStream input = resource.getInputStream();
      XSSFWorkbook xssfWorkbook = new XSSFWorkbook(input);

      XSSFSheet xssfSheet = xssfWorkbook.getSheet("7 answers");

      Iterator<Row> rowIterator = xssfSheet.iterator();

      rowIterator.next();

      int answers = 7;

      while (rowIterator.hasNext()) {
        Row row = rowIterator.next();

        Question question = new Question();
        question.setQuestionText(row.getCell(0).getStringCellValue());

        for (int i = 1; i < answers * 2; i += 2) {
          Answer answer = new Answer();
          answer.setAnswerText(getCellValue(row.getCell(i)));
          answer.setPoints(Double.valueOf(getCellValue(row.getCell(i + 1))));
          answer.setAnswerType(AnswerType.UNANSWERED);
          question.getAnswerList().add(answer);
        }
        questionList.add(question);
      }

    } catch (Exception ex) {
      Logger.getGlobal().log(Level.FINE, ex.toString());
    }
  }

  @Scheduled(fixedRate = 20000)
  public void askQuestions() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + secretKey);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    HashMap<String, String> jsonEntity = new HashMap<>();
    jsonEntity.put("channel", FAMILY_FEUD_CHANNEL);

    Question currentQuestion = questionList.get(random.nextInt(questionList.size()));

    jsonEntity.put("text", currentQuestion.getQuestionText());

    HttpEntity<HashMap> request = new HttpEntity<>(jsonEntity, httpHeaders);

    restTemplate.postForEntity("https://slack.com/api/chat.postMessage", request, String.class);

  }

  @Scheduled(fixedRate = 4000)
  public void monitorChat() throws Exception {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + secretKey);
    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    LinkedMultiValueMap<String, String> jsonEntity = new LinkedMultiValueMap<>();
    jsonEntity.add("channel", FAMILY_FEUD_CHANNEL);
    jsonEntity.add("oldest", currentTime.toString());
    jsonEntity.add("limit","50");

    HttpEntity<LinkedMultiValueMap> request = new HttpEntity<>(jsonEntity, httpHeaders);

    ResponseEntity<String> slackHistoryResponse = restTemplate.postForEntity("https://slack.com/api/conversations.history", request, String.class);

    ObjectMapper objectMapper = new ObjectMapper().configure(
        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    SlackHistoryResponse response = objectMapper.readValue(slackHistoryResponse.getBody(), SlackHistoryResponse.class);

    response.getMessages().forEach(this::checkAnswer);

  }

  private void checkAnswer(SlackMessage message) {

    currentQuestion.getAnswerList().stream()
        .filter(answer -> answer.getAnswerType().equals(AnswerType.UNANSWERED))
        .filter(answer -> answer.getAnswerText().contains(message.getText()))
        .findFirst()
        .ifPresentOrElse(
            System.out::println,
            () -> System.out.println("nope")
        );





  }








  private String getCellValue(Cell cell) {
    switch (cell.getCellType()) {
      case NUMERIC:
        return String.valueOf(cell.getNumericCellValue());
      case STRING:
        return cell.getStringCellValue();
      default:
        return "";
    }
  }


}