package com.example.aidevopsdemo.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

/**
 * Configuration for OpenAI API integration
 */
@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.timeout:30}")
    private Integer timeout;

    /**
     * Creates and configures the OpenAI client
     * This will be used for code generation, analysis, and automated documentation
     */
    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openaiApiKey, Duration.ofSeconds(timeout));
    }
}