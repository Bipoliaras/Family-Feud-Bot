package com.ernestas.familyfeudbot.question;

import com.ernestas.familyfeudbot.answer.Answer;
import com.ernestas.familyfeudbot.slack.SlackEndpoint;
import com.ernestas.familyfeudbot.slack.SlackMessage;
import org.junit.Test;

public class SlackEndpointTests {


  private SlackEndpoint slackEndpoint = new SlackEndpoint();

  @Test
  public void getConversationHistoryResponseOk() throws Exception {
    slackEndpoint.getConversationHistoryResponse();
  }

  @Test
  public void replyToUserOk() {
    slackEndpoint.replyToUser(SlackMessage.builder().ts("151454545").build(), Answer.builder().points(40.0).build());
  }



}
