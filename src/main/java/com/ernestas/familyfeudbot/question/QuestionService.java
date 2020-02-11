package com.ernestas.familyfeudbot.question;

import com.ernestas.familyfeudbot.answer.Answer;
import com.ernestas.familyfeudbot.answer.AnswerType;
import com.ernestas.familyfeudbot.slack.SlackEndpoint;
import com.ernestas.familyfeudbot.slack.SlackMessage;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

  private List<Question> questionList = new ArrayList<>();

  private Question currentQuestion;

  private Random random = new Random();

  private SlackEndpoint slackEndpoint;

  private HazelcastInstance instance = Hazelcast.newHazelcastInstance();

  @Autowired
  public void setSlackEndpoint(SlackEndpoint slackEndpoint) {
    this.slackEndpoint = slackEndpoint;
  }


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

    currentQuestion = questionList.get(random.nextInt(questionList.size()));

    slackEndpoint.askQuestion(currentQuestion);

  }

  @Scheduled(fixedRate = 5000, initialDelay = 10000)
  public void monitorChat() throws Exception {
    slackEndpoint.getConversationHistoryResponse()
        .getMessages()
        .forEach(this::checkAnswer);
  }

  private void checkAnswer(SlackMessage message) {

    currentQuestion.getAnswerList().stream()
        .filter(answer -> answer.getAnswerType().equals(AnswerType.UNANSWERED))
        .filter(answer -> answer.getAnswerText().equals(message.getText()))
        .findFirst()
        .ifPresent(
            answer ->
            {
              answer.setAnswerType(AnswerType.ANSWERED);
              awardPoints(message);
            }
        );
  }

  private void awardPoints(SlackMessage message) {
    slackEndpoint.replyToUser(message);
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

  public void setCurrentQuestion(Question currentQuestion) {
    this.currentQuestion = currentQuestion;
  }

  public void setQuestionList(List<Question> questionList) {
    this.questionList = questionList;
  }


}