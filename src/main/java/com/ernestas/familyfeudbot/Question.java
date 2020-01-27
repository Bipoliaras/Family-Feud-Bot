package com.ernestas.familyfeudbot;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {
  private String questionText;
  private ArrayList<Answer> answerList = new ArrayList<>();
}
