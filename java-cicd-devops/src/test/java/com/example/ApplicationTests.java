package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        // Verify the application context loads
    }
    
    @Test
    public void shouldReturnDefaultMessage() throws Exception {
        // Test the main endpoint
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string("Welcome to Java CI/CD Demo!"));
    }
    
    @Test
    public void healthEndpointShouldReturnUp() throws Exception {
        // Test the health endpoint
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("UP"));
    }
}