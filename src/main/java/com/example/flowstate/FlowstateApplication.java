package com.example.flowstate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlowstateApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowstateApplication.class, args);
	}

}
