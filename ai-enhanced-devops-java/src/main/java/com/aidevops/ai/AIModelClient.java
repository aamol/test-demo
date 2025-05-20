package com.aidevops.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Client for interacting with remote AI services
 */
public class AIModelClient {

    private static final Logger logger = LoggerFactory.getLogger(AIModelClient.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${ai.service.base-url}")
    private String aiServiceBaseUrl;
    
    @Value("${ai.service.api-key}")
    private String apiKey;
    
    private final HttpClient httpClient;
    
    public AIModelClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
    
    /**
     * Sends metrics to AI service for anomaly detection
     * 
     * @param metrics System metrics to analyze
     * @return Analysis results from AI service
     */
    public Map<String, Object> analyzeMetrics(Map<String, Object> metrics) throws Exception {
        String endpoint = aiServiceBaseUrl + "/analyze-metrics";
        return callAIService(endpoint, metrics);
    }
    
    /**
     * Requests remediation recommendation from AI service
     * 
     * @param metrics System metrics for context
     * @return Recommendation details from AI service
     */
    public Map<String, Object> generateRecommendation(Map<String, Object> metrics) throws Exception {
        String endpoint = aiServiceBaseUrl + "/generate-recommendation";
        return callAIService(endpoint, metrics);
    }
    
    /**
     * Evaluates whether automatic remediation is appropriate
     * 
     * @param metrics System metrics for context
     * @return Evaluation results from AI service
     */
    public Map<String, Object> evaluateAutoRemediation(Map<String, Object> metrics) throws Exception {
        String endpoint = aiServiceBaseUrl + "/evaluate-remediation";
        return callAIService(endpoint, metrics);
    }
    
    /**
     * Common method to make HTTP requests to AI services
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> callAIService(String endpoint, Map<String, Object> payload) throws Exception {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("X-API-Key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), Map.class);
            } else {
                logger.error("AI service error: {}, {}", response.statusCode(), response.body());
                throw new RuntimeException("AI service error: " + response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Error calling AI service", e);
            throw e;
        }
    }
}