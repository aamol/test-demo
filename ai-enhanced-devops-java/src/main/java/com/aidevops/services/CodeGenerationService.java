package com.aidevops.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service that interacts with an AI model to generate Java code
 * from natural language descriptions
 */
@Service
public class CodeGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CodeGenerationService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${openai.api-key}")
    private String openAiApiKey;
    
    @Value("${openai.model:gpt-4}")
    private String openAiModel;
    
    private final HttpClient httpClient;
    
    public CodeGenerationService() {
        this.httpClient = HttpClient.newHttpClient();
    }
    
    /**
     * Generate Java code from a natural language description
     * using an AI language model
     * 
     * @param description Natural language description of desired code
     * @return Generated Java code
     */
    public String generateJavaCode(String description) {
        try {
            // Prepare the prompt with specific instructions for Java code
            String prompt = "Generate well-documented Java code for the following requirement:\n\n" + 
                           description + 
                           "\n\nThe code should follow best practices and include comments.";
            
            // Create request payload
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", openAiModel);
            requestBody.put("messages", new Map[] {
                Map.of("role", "system", "content", "You are an expert Java developer assistant."),
                Map.of("role", "user", "content", prompt)
            });
            requestBody.put("temperature", 0.3); // Lower temperature for more predictable code
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            // Send request to OpenAI API
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openAiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
                
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Parse and extract the generated code
                Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
                Map<String, Object> choice = ((Map<String, Object>[])responseMap.get("choices"))[0];
                Map<String, String> message = (Map<String, String>)choice.get("message");
                String generatedCode = message.get("content");
                
                // Log success metrics
                logger.info("Successfully generated code for request: {}", description.substring(0, 
                    Math.min(description.length(), 50)) + "...");
                
                return generatedCode;
            } else {
                logger.error("Error from OpenAI API: {} {}", response.statusCode(), response.body());
                return "Error generating code: " + response.statusCode();
            }
        } catch (Exception e) {
            logger.error("Exception during code generation", e);
            return "Error generating code: " + e.getMessage();
        }
    }
}