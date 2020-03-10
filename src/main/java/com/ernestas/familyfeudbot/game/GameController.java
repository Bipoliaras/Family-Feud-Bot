package com.ernestas.familyfeudbot.game;

import com.ernestas.familyfeudbot.question.QuestionLoader;
import com.ernestas.familyfeudbot.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/feud")
public class GameController {

  private QuestionService questionService;

  private QuestionLoader questionLoader;

  @Autowired
  public void setQuestionService(QuestionService questionService) {
    this.questionService = questionService;
  }

  @Autowired
  public void setQuestionLoader(QuestionLoader questionLoader) {
    this.questionLoader = questionLoader;
  }

  @PostMapping("/play")
  public void play() {
    questionService.setQuestionList(questionLoader.getQuestions());
  }


  @GetMapping("/score")
  public void score() {

  }


}
