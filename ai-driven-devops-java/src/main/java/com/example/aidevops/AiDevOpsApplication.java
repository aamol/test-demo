package com.example.aidevops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class that demonstrates AI-driven DevOps principles
 * for Java developers.
 */
@SpringBootApplication
@EnableScheduling
public class AiDevOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDevOpsApplication.class, args);
    }
}