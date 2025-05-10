package com.example.javadevopsdemo.controller;

import com.example.javadevopsdemo.model.Task;
import com.example.javadevopsdemo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class DemoControllerTest {
    
    @Mock
    private TaskService taskService;
    
    @InjectMocks
    private DemoController controller;
    
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    public void testGetAllTasks() {
        // Prepare mock data
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        List<Task> tasks = Arrays.asList(task1, task2);
        
        // Mock service response
        when(taskService.findAllTasks()).thenReturn(tasks);
        
        // Execute the controller method
        ResponseEntity<List<Task>> response = controller.getAllTasks();
        
        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(taskService, times(1)).findAllTasks();
    }
    
    @Test
    public void testGetTaskById_Found() {
        // Prepare mock data
        Task task = new Task("Task 1", "Description 1");
        task.setId(1L);
        
        // Mock service response
        when(taskService.findTaskById(1L)).thenReturn(Optional.of(task));
        
        // Execute the controller method
        ResponseEntity<Task> response = controller.getTaskById(1L);
        
        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Task 1", response.getBody().getTitle());
        verify(taskService, times(1)).findTaskById(1L);
    }
    
    @Test
    public void testGetTaskById_NotFound() {
        // Mock service response for non-existent task
        when(taskService.findTaskById(99L)).thenReturn(Optional.empty());
        
        // Execute the controller method
        ResponseEntity<Task> response = controller.getTaskById(99L);
        
        // Verify the result
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(taskService, times(1)).findTaskById(99L);
    }
    
    @Test
    public void testCreateTask() {
        // Prepare mock data
        Task newTask = new Task("New Task", "New Description");
        Task savedTask = new Task("New Task", "New Description");
        savedTask.setId(1L);
        
        // Mock service response
        when(taskService.saveTask(any(Task.class))).thenReturn(savedTask);
        
        // Execute the controller method
        ResponseEntity<Task> response = controller.createTask(newTask);
        
        // Verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(taskService, times(1)).saveTask(any(Task.class));
    }
}