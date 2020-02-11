package com.ernestas.familyfeudbot.player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "t_player")
public class Player {

  @Id
  @Column(name = "player_name")
  private String playerName;

  @Column(name = "points")
  private double points;

}
