package com.aidevops;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.aidevops.monitoring.AIEnhancedMonitor;
import com.aidevops.monitoring.AnomalyDetector;
import com.aidevops.monitoring.MetricsCollector;

/**
 * Tests for the AI-enhanced monitoring system
 */
@ExtendWith(MockitoExtension.class)
public class AIEnhancedMonitorTest {

    @Mock
    private MetricsCollector metricsCollector;
    
    @Mock
    private AnomalyDetector anomalyDetector;
    
    @InjectMocks
    private AIEnhancedMonitor monitor;
    
    private Map<String, Object> testMetrics;
    
    @BeforeEach
    public void setup() {
        // Create test metrics
        testMetrics = new HashMap<>();
        testMetrics.put("heapMemoryUsed", 1024000000L);
        testMetrics.put("heapMemoryMax", 2048000000L);
        testMetrics.put("systemLoad", 0.75);
        testMetrics.put("threadCount", 25);
        
        // Set up the monitor with mocks
        ReflectionTestUtils.setField(monitor, "metricsCollector", metricsCollector);
        ReflectionTestUtils.setField(monitor, "anomalyDetector", anomalyDetector);
    }
    
    @Test
    public void testMonitorSystemWithNoAnomalies() {
        // Arrange
        when(metricsCollector.collectMetrics()).thenReturn(testMetrics);
        when(anomalyDetector.detectAnomalies(testMetrics)).thenReturn(false);
        
        // Act
        monitor.monitorSystem();
        
        // Assert
        verify(metricsCollector, times(1)).collectMetrics();
        verify(anomalyDetector, times(1)).detectAnomalies(testMetrics);
        verify(anomalyDetector, never()).generateRecommendation(any());
    }
    
    @Test
    public void testMonitorSystemWithAnomaliesDetected() {
        // Arrange
        when(metricsCollector.collectMetrics()).thenReturn(testMetrics);
        when(anomalyDetector.detectAnomalies(testMetrics)).thenReturn(true);
        when(anomalyDetector.generateRecommendation(testMetrics))
            .thenReturn("Consider increasing heap memory");
        when(anomalyDetector.shouldAutoRemediate(testMetrics)).thenReturn(false);
        
        // Act
        monitor.monitorSystem();
        
        // Assert
        verify(metricsCollector, times(1)).collectMetrics();
        verify(anomalyDetector, times(1)).detectAnomalies(testMetrics);
        verify(anomalyDetector, times(1)).generateRecommendation(testMetrics);
        verify(anomalyDetector, times(1)).shouldAutoRemediate(testMetrics);
    }
    
    @Test
    public void testMonitorSystemWithAutoRemediation() {
        // Arrange
        when(metricsCollector.collectMetrics()).thenReturn(testMetrics);
        when(anomalyDetector.detectAnomalies(testMetrics)).thenReturn(true);
        when(anomalyDetector.generateRecommendation(testMetrics))
            .thenReturn("Database connection pool needs scaling");
        when(anomalyDetector.shouldAutoRemediate(testMetrics)).thenReturn(true);
        
        // Act
        monitor.monitorSystem();
        
        // Assert
        verify(metricsCollector, times(1)).collectMetrics();
        verify(anomalyDetector, times(1)).detectAnomalies(testMetrics);
        verify(anomalyDetector, times(1)).generateRecommendation(testMetrics);
        verify(anomalyDetector, times(1)).shouldAutoRemediate(testMetrics);
        // Here we would also verify that remediation was attempted but this is
        // limited by our current implementation which doesn't expose this detail
    }
}