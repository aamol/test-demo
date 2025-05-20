package com.aidevops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.aidevops.monitoring.AIEnhancedMonitor;

/**
 * Main application entry point
 * AI-enhanced Java web application demonstrating DevOps integration
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public AIEnhancedMonitor aiMonitor() {
        // Initialize the AI-powered monitoring system
        return new AIEnhancedMonitor();
    }
}