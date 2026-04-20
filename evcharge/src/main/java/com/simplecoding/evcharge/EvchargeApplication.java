package com.simplecoding.evcharge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EvchargeApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvchargeApplication.class, args);
	}

}
