package com.alterna.store.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoreApplication {

	public static void main(String[] args) {
		// If no profile and no DB password from env, assume local run and use application-local.properties
		String profile = System.getenv("SPRING_PROFILES_ACTIVE");
		String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
		if ((profile == null || profile.isBlank()) && (password == null || password.isBlank())) {
			System.setProperty("spring.profiles.active", "local");
		}
		SpringApplication.run(StoreApplication.class, args);
	}

}
