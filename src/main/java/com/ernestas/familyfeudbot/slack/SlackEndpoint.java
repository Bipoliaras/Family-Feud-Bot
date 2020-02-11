package com.ernestas.familyfeudbot.slack;

import com.ernestas.familyfeudbot.answer.Answer;
import com.ernestas.familyfeudbot.question.Question;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SlackEndpoint {

  private String secretKey;

  @Value("${slack.secret.key}")
  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  private RestTemplate restTemplate = new RestTemplate();

  private ObjectMapper objectMapper =
      new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  private final String FAMILY_FEUD_CHANNEL = "CSSUB5Z8U";

  private static final int MESSAGE_LIMIT = 50;

  public SlackConversationHistoryResponse getConversationHistoryResponse() throws Exception {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + secretKey);
    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    LinkedMultiValueMap<String, Object> jsonEntity = new LinkedMultiValueMap<>();
    jsonEntity.add("channel", FAMILY_FEUD_CHANNEL);
    jsonEntity.add("limit", MESSAGE_LIMIT);

    HttpEntity<LinkedMultiValueMap> request = new HttpEntity<>(jsonEntity, httpHeaders);

    ResponseEntity<String> slackHistoryResponse =
        restTemplate.postForEntity(
            "https://slack.com/api/conversations.history", request, String.class);

    SlackConversationHistoryResponse response =
        objectMapper.readValue(slackHistoryResponse.getBody(), SlackConversationHistoryResponse.class);

    return response;
  }

  public void askQuestion(Question question) {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + secretKey);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    HashMap<String, String> jsonEntity = new HashMap<>();
    jsonEntity.put("channel", FAMILY_FEUD_CHANNEL);
    jsonEntity.put("text", question.getQuestionText());

    HttpEntity<HashMap> request = new HttpEntity<>(jsonEntity, httpHeaders);

    restTemplate.postForEntity("https://slack.com/api/chat.postMessage", request, String.class);

  }

  public void replyToUser(SlackMessage slackMessage, Answer answer) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + secretKey);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    HashMap<String, String> jsonEntity = new HashMap<>();
    jsonEntity.put("channel", FAMILY_FEUD_CHANNEL);
    jsonEntity.put("text", "Good job! You get " + answer.getPoints() + " points");
    jsonEntity.put("thread_ts", slackMessage.getTs());

    HttpEntity<HashMap> request = new HttpEntity<>(jsonEntity, httpHeaders);

    restTemplate.postForEntity("https://slack.com/api/chat.postMessage", request, String.class);

  }


}
