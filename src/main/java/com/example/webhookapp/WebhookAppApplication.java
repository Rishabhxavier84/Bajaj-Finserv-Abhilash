package com.example.webhookapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.example.webhookapp.service.WebhookService;

@SpringBootApplication
public class WebhookAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebhookAppApplication.class, args);
	}

	@Bean
	CommandLineRunner run(WebhookService webhookService) {
		return args -> webhookService.executeFlow();
	}
}