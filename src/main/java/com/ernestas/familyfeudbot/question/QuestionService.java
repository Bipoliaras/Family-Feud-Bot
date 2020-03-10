package com.ernestas.familyfeudbot.question;

import com.ernestas.familyfeudbot.answer.Answer;
import com.ernestas.familyfeudbot.answer.AnswerType;
import com.ernestas.familyfeudbot.award.AwardService;
import com.ernestas.familyfeudbot.player.Player;
import com.ernestas.familyfeudbot.slack.SlackEndpoint;
import com.ernestas.familyfeudbot.slack.SlackMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

  private List<Question> questionList = new ArrayList<>();

  private Question currentQuestion;

  private Random random = new Random();

  private SlackEndpoint slackEndpoint;

  private AwardService awardService;

  public static final int MAX_POINTS = 50;

  private final Logger logger = LoggerFactory.getLogger(QuestionService.class);

  @Scheduled(fixedRate = 20000)
  public void askQuestions() {
    if (questionList != null && !questionList.isEmpty()) {
      currentQuestion = questionList.get(random.nextInt(questionList.size()));
      logger.info("Current question:" + currentQuestion);
      slackEndpoint.askQuestion(currentQuestion);
    } else {
      logger.debug("Question list is empty, not asking any questions");
    }
  }

  @Scheduled(fixedRate = 5000, initialDelay = 10000)
  public void monitorChat() {
    if (currentQuestion != null) {
      slackEndpoint.getConversationHistoryResponse().getMessages().forEach(this::checkAnswer);
    } else {
      logger.debug("No question has been asked");
    }
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
    Player player = awardService.awardPoints(message.getUser(), answer.getPoints());
    if (player.getPoints() > MAX_POINTS) {
      gameOver();
      slackEndpoint.announceWinner(player.getPlayerName(), player.getPoints());
    } else {
      slackEndpoint.replyToUser(message, answer);
    }
  }

  public void setCurrentQuestion(Question currentQuestion) {
    this.currentQuestion = currentQuestion;
  }

  public void setQuestionList(List<Question> questionList) {
    this.questionList = questionList;
  }

  public void gameOver() {
    questionList = null;
    currentQuestion = null;
    awardService.resetPlayerPoints();
  }

  @Autowired
  public void setSlackEndpoint(SlackEndpoint slackEndpoint) {
    this.slackEndpoint = slackEndpoint;
  }

  @Autowired
  public void setAwardService(AwardService awardService) {
    this.awardService = awardService;
  }
}
