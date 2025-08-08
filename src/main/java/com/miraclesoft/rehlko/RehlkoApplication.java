package com.miraclesoft.rehlko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "com.miraclesoft.rehlko.repository")
public class RehlkoApplication {

	
	
	public static void main(String[] args) {
		SpringApplication.run(RehlkoApplication.class, args);
	}

}