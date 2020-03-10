package com.ernestas.familyfeudbot.question;

import static org.assertj.core.api.Assertions.assertThat;

import com.ernestas.familyfeudbot.FamilyFeudBotApplication;
import com.ernestas.familyfeudbot.award.AwardService;
import com.ernestas.familyfeudbot.player.Player;
import com.ernestas.familyfeudbot.player.PlayerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={FamilyFeudBotApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/db/delete-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/db/delete-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class AwardServiceTests {


  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private AwardService awardService;


  @Before
  public void setup() {
    playerRepository.save(new Player("Todd", 20));
  }

  @Test
  public void givenExistingPlayer_whenAwardPoints_ok() {
    awardService.awardPoints("Todd", 40);
    assertThat(playerRepository.findById("Todd").get().getPoints()).isEqualTo(60);
  }

  @Test
  public void givenNonExistingPlayer_whenAwardPoints_ok() {
    awardService.awardPoints("James", 40);
    assertThat(playerRepository.findById("James").get().getPoints()).isEqualTo(40);
  }

}
