package com.example.javadevopsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;

@SpringBootApplication
public class JavaDevopsDemoApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(JavaDevopsDemoApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Java DevOps Demo Application");
        SpringApplication.run(JavaDevopsDemoApplication.class, args);
    }
    
    @PostConstruct
    public void logStartup() {
        logger.info("Application initialized and ready for requests");
    }
}