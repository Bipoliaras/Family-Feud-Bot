package com.ernestas.familyfeudbot.question;

import static org.mockito.Mockito.when;

import com.ernestas.familyfeudbot.answer.Answer;
import com.ernestas.familyfeudbot.answer.AnswerType;
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

    questionService.setCurrentQuestion(Question.builder()
    .questionText("Name a big animal encounter in the United states")
    .answerList(List.of(
        Answer.builder().answerText("Donkey").points(15.0).answerType(AnswerType.UNANSWERED).build(),
        Answer.builder().answerText("Kangaroo").points(25.0).answerType(AnswerType.UNANSWERED).build(),
        Answer.builder().answerText("Hippo").points(35.0).answerType(AnswerType.UNANSWERED).build()
    )).build());

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

    questionService.setQuestionList(List.of(
        Question.builder()
            .questionText("Name a big animal encounter in the United states")
            .answerList(List.of(
                Answer.builder().answerText("Donkey").points(15.0).answerType(AnswerType.UNANSWERED).build(),
                Answer.builder().answerText("Kangaroo").points(25.0).answerType(AnswerType.UNANSWERED).build(),
                Answer.builder().answerText("Hippo").points(35.0).answerType(AnswerType.UNANSWERED).build()
            )).build()
    ));

    questionService.askQuestions();

    Mockito.verify(slackEndpoint, Mockito.times(1)).askQuestion(Mockito.any());
  }

  @Test
  public void questionServiceMonitorChatOk() throws Exception {
    Question question = new Question();
    question.setQuestionText("K?");
    question.setAnswerList(List.of(Answer.builder().answerText("parrot").points(20.0).answerType(
        AnswerType.UNANSWERED).build()));
    questionService.monitorChat();
  }
}
