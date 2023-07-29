package com.finance.adam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class AdamApplication {
	public static void main(String[] args) {
		SpringApplication.run(AdamApplication.class, args);
	}

}
