package com.ernestas.familyfeudbot.slack;

import java.util.ArrayList;
import lombok.Data;

@Data
public class SlackConversationHistoryResponse {
  private ArrayList<SlackMessage> messages = new ArrayList<>();
}
