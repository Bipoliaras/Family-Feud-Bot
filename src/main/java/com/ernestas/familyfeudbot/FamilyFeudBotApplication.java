package com.ernestas.familyfeudbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FamilyFeudBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(FamilyFeudBotApplication.class, args);
	}

}
