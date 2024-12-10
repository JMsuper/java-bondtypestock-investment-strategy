package com.finance.adam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdamApplication {
	public static void main(String[] args) {
		SpringApplication.run(AdamApplication.class, args);
	}

}
