package com.aidevops.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aidevops.services.CodeGenerationService;

/**
 * Demo controller for showcasing AI-enhanced capabilities
 */
@RestController
public class DemoController {

    @Autowired
    private CodeGenerationService codeGenService;

    @GetMapping("/api/health")
    public String healthCheck() {
        return "Service is healthy!";
    }
    
    /**
     * Endpoint that demonstrates AI-generated code based on natural language description
     */
    @PostMapping("/api/generate-code")
    public String generateCode(@RequestBody String description) {
        return codeGenService.generateJavaCode(description);
    }
}