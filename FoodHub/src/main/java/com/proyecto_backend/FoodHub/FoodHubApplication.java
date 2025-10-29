package com.proyecto_backend.FoodHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FoodHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodHubApplication.class, args);
	}

}
