package com.foodroute.order.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableKafka
@SpringBootApplication
public class FoodrouteOrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodrouteOrderServiceApplication.class, args);
	}

}
