package com.example.aidevops.controller;

import com.example.aidevops.ai.CodeGenerationService;
import com.example.aidevops.cicd.PredictiveCiCdService;
import com.example.aidevops.monitoring.AnomalyDetectionService;
import com.example.aidevops.testing.TestGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demo controller showcasing various AI-driven capabilities
 * for DevOps integration.
 */
@RestController
@RequestMapping("/api/ai")
public class AiDemoController {

    private final CodeGenerationService codeGenService;
    private final TestGenerationService testGenService;
    private final PredictiveCiCdService predictiveCiCdService;
    private final AnomalyDetectionService anomalyDetectionService;

    @Autowired
    public AiDemoController(
            CodeGenerationService codeGenService,
            TestGenerationService testGenService,
            PredictiveCiCdService predictiveCiCdService,
            AnomalyDetectionService anomalyDetectionService) {
        this.codeGenService = codeGenService;
        this.testGenService = testGenService;
        this.predictiveCiCdService = predictiveCiCdService;
        this.anomalyDetectionService = anomalyDetectionService;
    }

    /**
     * Endpoint to demonstrate AI-powered code generation
     */
    @PostMapping("/generate-code")
    public ResponseEntity<Map<String, String>> generateCode(@RequestBody Map<String, String> request) {
        String description = request.getOrDefault("description", "");
        String generatedCode = codeGenService.generateCode(description);
        
        Map<String, String> response = new HashMap<>();
        response.put("generatedCode", generatedCode);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint to demonstrate AI-powered test generation
     */
    @GetMapping("/generate-tests")
    public ResponseEntity<List<TestGenerationService.TestCase>> generateTests(
            @RequestParam String className) {
        List<TestGenerationService.TestCase> testCases = testGenService.generateTestCases(className);
        return ResponseEntity.ok(testCases);
    }
    
    /**
     * Endpoint to demonstrate AI-powered build risk assessment
     */
    @PostMapping("/assess-build-risk")
    public ResponseEntity<PredictiveCiCdService.BuildRiskAssessment> assessBuildRisk(
            @RequestBody Map<String, Integer> codeChanges) {
        PredictiveCiCdService.BuildRiskAssessment assessment = 
                predictiveCiCdService.predictBuildSuccess(codeChanges);
        return ResponseEntity.ok(assessment);
    }
    
    /**
     * Endpoint to submit a metric for anomaly detection
     */
    @PostMapping("/detect-anomaly")
    public ResponseEntity<Map<String, Object>> detectAnomaly(
            @RequestBody Map<String, Object> metricData) {
        String metricName = (String) metricData.get("name");
        double metricValue = ((Number) metricData.get("value")).doubleValue();
        
        AnomalyDetectionService.MetricDataPoint metric = 
                new AnomalyDetectionService.MetricDataPoint(metricName, metricValue);
                
        boolean isAnomaly = anomalyDetectionService.detectAnomaly(metric);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isAnomaly", isAnomaly);
        response.put("metricName", metricName);
        response.put("metricValue", metricValue);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}