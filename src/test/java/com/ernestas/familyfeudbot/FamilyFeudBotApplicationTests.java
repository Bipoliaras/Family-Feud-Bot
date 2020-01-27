package com.ernestas.familyfeudbot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FamilyFeudBotApplicationTests {

	@Test
	public void simpleTest() {
		assertThat(10).isEqualTo(10);
	}

}
