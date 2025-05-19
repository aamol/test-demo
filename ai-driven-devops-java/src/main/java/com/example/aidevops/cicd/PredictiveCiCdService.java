package com.example.aidevops.cicd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Service for implementing AI-enhanced CI/CD capabilities
 * that can predict build failures before they occur.
 */
@Service
public class PredictiveCiCdService {
    private static final Logger logger = LoggerFactory.getLogger(PredictiveCiCdService.class);
    
    private final Map<String, Double> componentRiskScores = new HashMap<>();
    private final Random random = new Random();
    
    /**
     * Analyzes the recent code changes to predict potential build failures.
     * In a real implementation, this would leverage an SLM trained on historical
     * build data to identify patterns that correlate with failures.
     *
     * @param codeChanges Map of files changed with their change size
     * @return Risk assessment of the build
     */
    public BuildRiskAssessment predictBuildSuccess(Map<String, Integer> codeChanges) {
        logger.info("Analyzing {} code changes for build risk prediction", codeChanges.size());
        
        double overallRiskScore = calculateRiskScore(codeChanges);
        
        BuildRiskAssessment assessment = new BuildRiskAssessment();
        assessment.setOverallRiskScore(overallRiskScore);
        assessment.setPredictedOutcome(overallRiskScore > 0.7 ? "LIKELY_FAIL" : "LIKELY_SUCCESS");
        
        // Add component-specific risks
        Map<String, String> componentRisks = new HashMap<>();
        for (Map.Entry<String, Double> entry : componentRiskScores.entrySet()) {
            if (entry.getValue() > 0.5) {
                componentRisks.put(entry.getKey(), 
                                  String.format("High risk (%.2f): Consider additional testing", 
                                               entry.getValue()));
            }
        }
        assessment.setComponentRisks(componentRisks);
        
        return assessment;
    }
    
    /**
     * Calculates a risk score based on code changes.
     * This is a simplified simulation. In a real implementation, this would
     * use an actual machine learning model.
     */
    private double calculateRiskScore(Map<String, Integer> codeChanges) {
        // Reset component risk scores
        componentRiskScores.clear();
        
        // Calculate a base risk score based on the number of changes
        double baseRiskScore = Math.min(0.1 + (codeChanges.size() * 0.05), 0.5);
        
        // Analyze each file change
        for (Map.Entry<String, Integer> entry : codeChanges.entrySet()) {
            String filename = entry.getKey();
            int changeSize = entry.getValue();
            
            // Calculate risk for this component
            double componentRisk = calculateComponentRisk(filename, changeSize);
            componentRiskScores.put(filename, componentRisk);
            
            // Increase overall risk based on component risk
            if (componentRisk > 0.7) {
                baseRiskScore += 0.1;
            }
        }
        
        // Cap the risk score at 1.0
        return Math.min(baseRiskScore, 1.0);
    }
    
    private double calculateComponentRisk(String filename, int changeSize) {
        // This logic would normally be handled by an ML model
        double baseRisk = 0.0;
        
        // Higher risk for larger changes
        baseRisk += Math.min(changeSize * 0.01, 0.3);
        
        // Higher risk for critical components
        if (filename.contains("security") || filename.contains("auth")) {
            baseRisk += 0.2;
        }
        
        // Higher risk for database-related changes
        if (filename.contains("repository") || filename.contains("dao")) {
            baseRisk += 0.15;
        }
        
        // Add some randomness to simulate AI prediction variance
        baseRisk += (random.nextDouble() * 0.1);
        
        return Math.min(baseRisk, 1.0);
    }
    
    public static class BuildRiskAssessment {
        private double overallRiskScore;
        private String predictedOutcome;
        private Map<String, String> componentRisks = new HashMap<>();
        
        public double getOverallRiskScore() {
            return overallRiskScore;
        }
        
        public void setOverallRiskScore(double overallRiskScore) {
            this.overallRiskScore = overallRiskScore;
        }
        
        public String getPredictedOutcome() {
            return predictedOutcome;
        }
        
        public void setPredictedOutcome(String predictedOutcome) {
            this.predictedOutcome = predictedOutcome;
        }
        
        public Map<String, String> getComponentRisks() {
            return componentRisks;
        }
        
        public void setComponentRisks(Map<String, String> componentRisks) {
            this.componentRisks = componentRisks;
        }
    }
}