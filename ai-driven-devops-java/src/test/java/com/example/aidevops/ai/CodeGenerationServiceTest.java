package com.example.aidevops.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the AI-driven code generation service.
 */
public class CodeGenerationServiceTest {

    @Test
    void shouldGenerateControllerCodeForRestControllerDescription() {
        // Arrange
        CodeGenerationService service = new CodeGenerationService();
        
        // Act
        String generatedCode = service.generateCode("Create a REST controller for user data");
        
        // Assert
        assertTrue(generatedCode.contains("@RestController"));
        assertTrue(generatedCode.contains("@RequestMapping"));
    }
    
    @Test
    void shouldGenerateRepositoryCodeForRepositoryDescription() {
        // Arrange
        CodeGenerationService service = new CodeGenerationService();
        
        // Act
        String generatedCode = service.generateCode("Need a repository for user entity");
        
        // Assert
        assertTrue(generatedCode.contains("@Repository"));
        assertTrue(generatedCode.contains("JpaRepository"));
    }
    
    @Test
    void shouldReturnDefaultMessageForUnknownDescription() {
        // Arrange
        CodeGenerationService service = new CodeGenerationService();
        
        // Act
        String generatedCode = service.generateCode("Something completely unrelated");
        
        // Assert
        assertTrue(generatedCode.contains("No code generation template found"));
    }
}