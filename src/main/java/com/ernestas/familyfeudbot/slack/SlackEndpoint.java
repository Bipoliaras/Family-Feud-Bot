package com.ernestas.familyfeudbot.slack;

import com.ernestas.familyfeudbot.answer.Answer;
import com.ernestas.familyfeudbot.question.Question;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static Logger logger = LoggerFactory.getLogger(SlackEndpoint.class);

  private static final String FAMILY_FEUD_CHANNEL = "CSSUB5Z8U";

  private static final int MESSAGE_LIMIT = 50;

  public SlackConversationHistoryResponse getConversationHistoryResponse() {

    try {

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

      return objectMapper.readValue(
          slackHistoryResponse.getBody(), SlackConversationHistoryResponse.class);

    } catch (Exception ex) {
      logger.error(ex.toString());
    }

    return null;
  }

  public String getRealName(String userId) {

    try {

      HttpEntity<String> request = new HttpEntity<>(getHeaders());

      String responseJson =
          restTemplate.postForObject(
              SlackUrls.USER_INFO.getUrl()+"?user="+ userId, request, String.class);

      JsonNode jsonRoot = objectMapper.readTree(responseJson);

      return jsonRoot.get("user").get("real_name").asText();

    } catch (Exception ex) {
      logger.error(ex.toString());
    }

    return "";
  }

  public void announceWinner(String userId, double points) {
    String realName = getRealName(userId);

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("channel", FAMILY_FEUD_CHANNEL);
    jsonObject.put(
        "text", "Game over! @" + realName + " is the winner today with " + points + " points");

    HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), getHeaders());

    restTemplate.postForObject(SlackUrls.POST_MESSAGE.getUrl(), request, String.class);
  }

  public void askQuestion(Question question) {

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("channel", FAMILY_FEUD_CHANNEL);
    jsonObject.put("text", question.getQuestionText());

    HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), getHeaders());

    restTemplate.postForEntity(SlackUrls.POST_MESSAGE.getUrl(), request, String.class);
  }

  public void replyToUser(SlackMessage slackMessage, Answer answer) {

    JSONObject jsonObject = new JSONObject();
    jsonObject.put("channel", FAMILY_FEUD_CHANNEL);
    jsonObject.put("text", "Good job! You get " + answer.getPoints() + " points");
    jsonObject.put("thread_ts", slackMessage.getTs());

    HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), getHeaders());

    restTemplate.postForEntity(SlackUrls.POST_MESSAGE.getUrl(), request, String.class);
  }

  public HttpHeaders getHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("Authorization", "Bearer " + secretKey);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    return httpHeaders;
  }
}
