package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("Java DevOps Demo Application Started!");
    }

    @GetMapping("/")
    public String hello() {
        return "Hello from Java DevOps Demo!";
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}