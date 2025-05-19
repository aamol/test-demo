package com.example.aidevops.incident;

import com.example.aidevops.monitoring.AnomalyDetectionService.MetricDataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service that uses AI to predict potential incidents before they occur
 * and suggest preventive measures.
 */
@Service
public class IncidentPredictionService {
    private static final Logger logger = LoggerFactory.getLogger(IncidentPredictionService.class);
    
    private final Map<String, List<MetricDataPoint>> metricHistory = new HashMap<>();
    
    /**
     * Records a new metric data point for incident prediction analysis
     */
    public void recordMetric(String metricName, double value) {
        MetricDataPoint dataPoint = new MetricDataPoint(metricName, value);
        
        // Initialize the history list if it doesn't exist
        metricHistory.putIfAbsent(metricName, new ArrayList<>());
        
        // Add the data point to the history
        List<MetricDataPoint> history = metricHistory.get(metricName);
        history.add(dataPoint);
        
        // Keep history to a manageable size
        if (history.size() > 100) {
            history.remove(0);
        }
    }
    
    /**
     * Scheduled task to analyze metrics and predict potential incidents
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    public void predictIncidents() {
        logger.info("Running incident prediction analysis...");
        
        List<IncidentPrediction> predictions = new ArrayList<>();
        
        // Analyze each metric for patterns that might indicate future incidents
        for (Map.Entry<String, List<MetricDataPoint>> entry : metricHistory.entrySet()) {
            String metricName = entry.getKey();
            List<MetricDataPoint> history = entry.getValue();
            
            if (history.size() < 10) {
                continue; // Not enough data to make a prediction
            }
            
            // Check for concerning trends
            IncidentPrediction prediction = analyzeTrend(metricName, history);
            if (prediction != null) {
                predictions.add(prediction);
                logger.warn("Potential incident predicted: {}", prediction);
            }
        }
        
        // In a real application, we would trigger alerts or remediation actions
        if (!predictions.isEmpty()) {
            logger.warn("Predicted {} potential incidents", predictions.size());
        }
    }
    
    /**
     * Analyzes a metric trend to predict potential incidents.
     * In a real application, this would use an SLM trained on historical incident data.
     */
    private IncidentPrediction analyzeTrend(String metricName, List<MetricDataPoint> history) {
        // Simple trend analysis for the example
        // In a real application, this would be handled by a machine learning model
        
        // Calculate the rate of change
        List<Double> recentValues = new ArrayList<>();
        for (int i = Math.max(0, history.size() - 5); i < history.size(); i++) {
            recentValues.add(history.get(i).getValue());
        }
        
        double averageChange = calculateAverageChange(recentValues);
        
        // Detect if this metric is showing a concerning trend
        if (isConcerningTrend(metricName, averageChange, recentValues)) {
            return createIncidentPrediction(metricName, averageChange, recentValues);
        }
        
        return null;
    }
    
    private double calculateAverageChange(List<Double> values) {
        if (values.size() < 2) return 0;
        
        double sum = 0;
        for (int i = 1; i < values.size(); i++) {
            sum += values.get(i) - values.get(i-1);
        }
        
        return sum / (values.size() - 1);
    }
    
    private boolean isConcerningTrend(String metricName, double averageChange, List<Double> values) {
        // Different metrics may have different thresholds for concern
        if (metricName.contains("memory") || metricName.contains("cpu")) {
            // For resource metrics, we're concerned about consistently increasing values
            return averageChange > 5.0 && values.get(values.size() - 1) > 70.0;
        } else if (metricName.contains("latency") || metricName.contains("response")) {
            // For performance metrics, we're concerned about any significant increase
            return averageChange > 10.0;
        } else if (metricName.contains("error") || metricName.contains("failure")) {
            // For error metrics, we're concerned about any increase from zero
            return averageChange > 0 && values.get(values.size() - 1) > 0;
        }
        
        // Default threshold
        return Math.abs(averageChange) > 20.0;
    }
    
    private IncidentPrediction createIncidentPrediction(String metricName, double averageChange, List<Double> values) {
        IncidentPrediction prediction = new IncidentPrediction();
        prediction.setMetricName(metricName);
        prediction.setCurrentValue(values.get(values.size() - 1));
        prediction.setAverageChangeRate(averageChange);
        
        // Set prediction details based on the metric type
        if (metricName.contains("memory")) {
            prediction.setPredictedIncident("Potential memory leak");
            prediction.setSeverity("MEDIUM");
            prediction.setTimeToIncident("~" + estimateTimeToIncident(values, 90.0) + " minutes");
            prediction.setRecommendedAction("Review recent code changes affecting memory usage");
        } else if (metricName.contains("cpu")) {
            prediction.setPredictedIncident("CPU saturation approaching");
            prediction.setSeverity("HIGH");
            prediction.setTimeToIncident("~" + estimateTimeToIncident(values, 95.0) + " minutes");
            prediction.setRecommendedAction("Scale up resources or identify CPU-intensive processes");
        } else if (metricName.contains("latency")) {
            prediction.setPredictedIncident("Performance degradation");
            prediction.setSeverity("MEDIUM");
            prediction.setTimeToIncident("~" + estimateTimeToIncident(values, values.get(values.size() - 1) * 2) + " minutes");
            prediction.setRecommendedAction("Check database queries and external service calls");
        } else {
            prediction.setPredictedIncident("Anomalous behavior detected");
            prediction.setSeverity("LOW");
            prediction.setTimeToIncident("Unknown");
            prediction.setRecommendedAction("Monitor the situation");
        }
        
        return prediction;
    }
    
    private int estimateTimeToIncident(List<Double> values, double threshold) {
        if (values.size() < 2) return 99;
        
        double lastValue = values.get(values.size() - 1);
        double averageChange = calculateAverageChange(values);
        
        if (averageChange <= 0) return 99; // Not increasing
        
        double valueToThreshold = threshold - lastValue;
        if (valueToThreshold <= 0) return 0; // Already at threshold
        
        // Estimate minutes until reaching threshold at current rate of change
        return (int) Math.ceil(valueToThreshold / averageChange);
    }
    
    public static class IncidentPrediction {
        private String metricName;
        private double currentValue;
        private double averageChangeRate;
        private String predictedIncident;
        private String severity;
        private String timeToIncident;
        private String recommendedAction;
        
        // Getters and setters
        public String getMetricName() {
            return metricName;
        }
        
        public void setMetricName(String metricName) {
            this.metricName = metricName;
        }
        
        public double getCurrentValue() {
            return currentValue;
        }
        
        public void setCurrentValue(double currentValue) {
            this.currentValue = currentValue;
        }
        
        public double getAverageChangeRate() {
            return averageChangeRate;
        }
        
        public void setAverageChangeRate(double averageChangeRate) {
            this.averageChangeRate = averageChangeRate;
        }
        
        public String getPredictedIncident() {
            return predictedIncident;
        }
        
        public void setPredictedIncident(String predictedIncident) {
            this.predictedIncident = predictedIncident;
        }
        
        public String getSeverity() {
            return severity;
        }
        
        public void setSeverity(String severity) {
            this.severity = severity;
        }
        
        public String getTimeToIncident() {
            return timeToIncident;
        }
        
        public void setTimeToIncident(String timeToIncident) {
            this.timeToIncident = timeToIncident;
        }
        
        public String getRecommendedAction() {
            return recommendedAction;
        }
        
        public void setRecommendedAction(String recommendedAction) {
            this.recommendedAction = recommendedAction;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Prediction: %s (Severity: %s, ETA: %s), Current value: %.2f, Rate: %.2f, Action: %s",
                predictedIncident, severity, timeToIncident, currentValue, averageChangeRate, recommendedAction
            );
        }
    }
}