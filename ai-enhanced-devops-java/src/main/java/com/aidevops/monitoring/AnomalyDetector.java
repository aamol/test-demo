package com.aidevops.monitoring;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.aidevops.ai.AIModelClient;

/**
 * Uses AI models to detect anomalies in system metrics
 * and predict potential issues before they occur
 */
public class AnomalyDetector {
    
    private static final Logger logger = LoggerFactory.getLogger(AnomalyDetector.class);
    
    @Value("${ai.model.threshold:0.75}")
    private double anomalyThreshold;
    
    private final AIModelClient aiClient;
    
    public AnomalyDetector() {
        this.aiClient = new AIModelClient();
    }
    
    /**
     * Analyzes collected metrics using an AI model to detect anomalies
     * @param metrics The system metrics to analyze
     * @return True if anomalies were detected
     */
    public boolean detectAnomalies(Map<String, Object> metrics) {
        try {
            // Call the AI model service to analyze metrics
            Map<String, Object> response = aiClient.analyzeMetrics(metrics);
            
            // Get anomaly score from model response
            double anomalyScore = (double) response.getOrDefault("anomalyScore", 0.0);
            
            // Log the details for debugging and analysis
            logger.debug("Anomaly detection completed. Score: {}, Threshold: {}", 
                        anomalyScore, anomalyThreshold);
            
            // Return true if score exceeds threshold
            return anomalyScore > anomalyThreshold;
        } catch (Exception e) {
            logger.error("Error during anomaly detection", e);
            return false;
        }
    }
    
    /**
     * Generates a recommendation for addressing detected anomalies
     * using an AI model trained on historical system behavior
     * 
     * @param metrics Current system metrics
     * @return Human-readable recommendation
     */
    public String generateRecommendation(Map<String, Object> metrics) {
        try {
            // Call the AI model service to generate recommendations
            Map<String, Object> response = aiClient.generateRecommendation(metrics);
            
            // Extract recommendation from response
            return (String) response.getOrDefault("recommendation", 
                    "Unable to generate specific recommendation");
        } catch (Exception e) {
            logger.error("Error generating recommendation", e);
            return "Error generating recommendation: " + e.getMessage();
        }
    }
    
    /**
     * Determines if automated remediation should be performed
     * for the detected anomalies
     * 
     * @param metrics Current system metrics
     * @return True if automatic remediation is recommended
     */
    public boolean shouldAutoRemediate(Map<String, Object> metrics) {
        try {
            // Call the AI service for remediation recommendation
            Map<String, Object> response = aiClient.evaluateAutoRemediation(metrics);
            
            // Extract confidence score for auto-remediation
            double confidence = (double) response.getOrDefault("confidence", 0.0);
            double confidenceThreshold = 0.9; // Only auto-remediate with high confidence
            
            return confidence > confidenceThreshold;
        } catch (Exception e) {
            logger.error("Error evaluating auto-remediation", e);
            return false; // Default to no auto-remediation on errors
        }
    }
}