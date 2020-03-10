package com.ernestas.familyfeudbot.question;

import com.ernestas.familyfeudbot.answer.Answer;
import com.ernestas.familyfeudbot.answer.AnswerType;
import com.ernestas.familyfeudbot.award.AwardService;
import com.ernestas.familyfeudbot.slack.SlackEndpoint;
import com.ernestas.familyfeudbot.slack.SlackMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

  private List<Question> questionList = new ArrayList<>();

  private Question currentQuestion;

  private Random random = new Random();

  private SlackEndpoint slackEndpoint;

  private QuestionLoader questionLoader;

  private AwardService awardService;

  @PostConstruct
  public void loadQuestions() {
    questionList = questionLoader.getQuestions();
  }

  @Scheduled(fixedRate = 20000)
  public void askQuestions() {
    currentQuestion = questionList.get(random.nextInt(questionList.size()));
    System.out.println(currentQuestion);
    slackEndpoint.askQuestion(currentQuestion);
  }

  @Scheduled(fixedRate = 5000, initialDelay = 10000)
  public void monitorChat() throws Exception {
    slackEndpoint.getConversationHistoryResponse().getMessages().forEach(this::checkAnswer);
  }

  private void checkAnswer(SlackMessage message) {

    currentQuestion.getAnswerList().stream()
        .filter(answer -> answer.getAnswerType().equals(AnswerType.UNANSWERED))
        .filter(answer -> answer.getAnswerText().equals(message.getText()))
        .findFirst()
        .ifPresent(
            answer -> {
              answer.setAnswerType(AnswerType.ANSWERED);
              awardPoints(message, answer);
            });
  }

  public void awardPoints(SlackMessage message, Answer answer) {
    awardService.awardPoints(message.getUser(), answer.getPoints());
    slackEndpoint.replyToUser(message, answer);
  }

  public void setCurrentQuestion(Question currentQuestion) {
    this.currentQuestion = currentQuestion;
  }

  public void setQuestionList(List<Question> questionList) {
    this.questionList = questionList;
  }

  @Autowired
  public void setSlackEndpoint(SlackEndpoint slackEndpoint) {
    this.slackEndpoint = slackEndpoint;
  }

  @Autowired
  public void setQuestionLoader(QuestionLoader questionLoader) {
    this.questionLoader = questionLoader;
  }

  @Autowired
  public void setAwardService(AwardService awardService) {
    this.awardService = awardService;
  }
}
