package com.ernestas.familyfeudbot;

import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SlackMessage {

  private String text;
  private String type;
  private String subtype;
  private double ts;
  private String bot_id;
  private String username;
  private String user;

}
