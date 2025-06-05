package com.crud.market_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class MarketApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(MarketApiApplication.class, args);
	}
}