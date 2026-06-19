package com.together.buytogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class BuyTogetherApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuyTogetherApplication.class, args);
	}

}
