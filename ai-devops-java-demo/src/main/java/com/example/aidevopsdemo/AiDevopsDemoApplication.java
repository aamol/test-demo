package com.example.aidevopsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the AI DevOps Demo
 */
@SpringBootApplication
@EnableScheduling
public class AiDevopsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDevopsDemoApplication.class, args);
    }
}