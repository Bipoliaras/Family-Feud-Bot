package com.ernestas.familyfeudbot.question;

import static org.mockito.Mockito.when;

import com.ernestas.familyfeudbot.slack.SlackConversationHistoryResponse;
import com.ernestas.familyfeudbot.slack.SlackEndpoint;
import com.ernestas.familyfeudbot.slack.SlackMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class QuestionServiceTests {


  private QuestionService questionService;

  private SlackEndpoint slackEndpoint;

  @Before
  public void setup() throws Exception {
    questionService = new QuestionService();
    questionService.loadQuestions();

    slackEndpoint = Mockito.mock(SlackEndpoint.class);
    when(slackEndpoint.getConversationHistoryResponse()).thenReturn(
        SlackConversationHistoryResponse.builder
            ().messages(
            List.of(
                SlackMessage.builder().text("parrot").user("user3").user("xD").build(),
                SlackMessage.builder().text("grass").user("user2").user("xD").build(),
                SlackMessage.builder().text("tree").user("user1").user("xD").build()
            )).build()
    );

    questionService.setSlackEndpoint(slackEndpoint);
  }

  @Test
  public void questionServiceAskQuestionOk() {
    questionService.askQuestions();
    Mockito.verify(slackEndpoint, Mockito.times(1)).askQuestion(Mockito.any());
  }

  @Test
  public void questionServiceMonitorChatOk() throws Exception {
    Question question = new Question();
    question.setQuestionText("K?");
    question.setAnswerList();
    questionService.monitorChat();
  }
}
