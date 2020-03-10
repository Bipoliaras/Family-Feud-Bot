package com.ernestas.familyfeudbot.award;

import com.ernestas.familyfeudbot.player.Player;
import com.ernestas.familyfeudbot.player.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AwardService {

  private PlayerRepository playerRepository;

  @Autowired
  public void setPlayerRepository(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  public void awardPoints(String name, double points) {
    playerRepository
        .findById(name)
        .ifPresentOrElse(
            player -> updatePlayerPoints(player, points),
            () -> createNewPlayer(name, points)
        );
  }

  private void updatePlayerPoints(Player player, double points) {
    player.setPoints(points + player.getPoints());
    playerRepository.save(player);
  }

  private void createNewPlayer(String name, double points) {
    Player player = new Player();
    player.setPoints(points);
    player.setPlayerName(name);
    playerRepository.save(player);
  }
}
