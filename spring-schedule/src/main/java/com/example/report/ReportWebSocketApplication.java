package com.example.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReportWebSocketApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReportWebSocketApplication.class, args);
	}
}
