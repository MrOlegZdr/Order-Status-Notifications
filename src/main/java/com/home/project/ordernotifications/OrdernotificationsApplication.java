package com.home.project.ordernotifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class OrdernotificationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdernotificationsApplication.class, args);
	}

}
