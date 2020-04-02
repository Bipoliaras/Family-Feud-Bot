package com.ernestas.familyfeudbot.question;

public enum QuestionSheet {

  THREE_ANSWERS("3 Answers"),
  FOUR_ANSWERS("4 Answers"),
  FIVE_ANSWERS("5 Answers"),
  SIX_ANSWERS("6 Answers"),
  SEVEN_ANSWERS("7 Answers");

  private String name;

  QuestionSheet(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
