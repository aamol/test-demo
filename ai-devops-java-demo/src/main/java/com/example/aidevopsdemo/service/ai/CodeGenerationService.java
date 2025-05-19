package com.example.aidevopsdemo.service.ai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for AI-assisted code generation using OpenAI API
 */
@Service
public class CodeGenerationService {

    private final OpenAiService openAiService;
    
    @Autowired
    public CodeGenerationService(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }
    
    /**
     * Generates Java code based on a natural language description
     * Uses the GPT models to create code snippets from requirements
     * 
     * @param description Natural language description of what the code should do
     * @return Generated Java code
     */
    public String generateJavaCode(String description) {
        List<ChatMessage> messages = new ArrayList<>();
        
        messages.add(new ChatMessage("system", 
            "You are an expert Java developer. Generate clean, efficient Java code based on the user's description. " +
            "Include comments and follow best practices. Only return the code, no explanations."));
        
        messages.add(new ChatMessage("user", "Generate Java code for: " + description));
        
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .messages(messages)
            .model("gpt-4")
            .maxTokens(2000)
            .build();
            
        String generatedCode = openAiService.createChatCompletion(completionRequest)
            .getChoices().get(0).getMessage().getContent();
            
        return cleanupGeneratedCode(generatedCode);
    }
    
    /**
     * Generates a unit test for a given Java class
     * 
     * @param className The name of the class to test
     * @param classCode The code of the class to test
     * @return Generated test code
     */
    public String generateUnitTest(String className, String classCode) {
        List<ChatMessage> messages = new ArrayList<>();
        
        messages.add(new ChatMessage("system", 
            "You are an expert in JUnit test writing. Given a Java class, generate comprehensive JUnit 5 tests " +
            "for that class. Follow testing best practices including proper assertions and test coverage."));
        
        messages.add(new ChatMessage("user", "Here is a Java class named " + className + ":\n\n" + classCode + 
            "\n\nGenerate a complete JUnit 5 test class for it."));
        
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .messages(messages)
            .model("gpt-4")
            .maxTokens(2000)
            .build();
            
        String generatedTest = openAiService.createChatCompletion(completionRequest)
            .getChoices().get(0).getMessage().getContent();
            
        return cleanupGeneratedCode(generatedTest);
    }
    
    /**
     * Cleans up the generated code by removing any markdown formatting
     * or additional text that may have been included in the AI response
     */
    private String cleanupGeneratedCode(String generatedCode) {
        // Remove markdown code block indicators if present
        if (generatedCode.startsWith("