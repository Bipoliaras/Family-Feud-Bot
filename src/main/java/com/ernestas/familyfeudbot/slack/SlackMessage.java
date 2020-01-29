package com.ernestas.familyfeudbot.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
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
