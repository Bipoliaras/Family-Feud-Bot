package com.ernestas.familyfeudbot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class SlackHistoryResponse {

  private String ok;
  private ArrayList<SlackMessage> messages = new ArrayList<>();

}
