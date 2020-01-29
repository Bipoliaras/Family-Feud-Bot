package com.ernestas.familyfeudbot.answer;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
  private String answerText;
  private Double points;
  private AnswerType answerType;
}
