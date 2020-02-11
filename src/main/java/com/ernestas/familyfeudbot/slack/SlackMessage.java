package com.ernestas.familyfeudbot.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SlackMessage {

  private String text;
  private String type;
  private String subtype;
  private String ts;
  @JsonProperty("bot_id")
  private String botId;
  private String username;
  private String user;

}
