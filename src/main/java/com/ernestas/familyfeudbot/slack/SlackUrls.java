package com.ernestas.familyfeudbot.slack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SlackUrls {

  POST_MESSAGE("https://slack.com/api/chat.postMessage"),
  USER_INFO("https://slack.com/api/users.info");

  private String url;

}
