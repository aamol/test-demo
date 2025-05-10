package com.example.javadevopsdemo.controller;

import com.example.javadevopsdemo.model.Task;
import com.example.javadevopsdemo.service.TaskService;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class DemoController {
    
    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);
    
    @Autowired
    private TaskService taskService;
    
    @GetMapping
    @Timed(value = "get.tasks", description = "Time taken to return all tasks")
    public ResponseEntity<List<Task>> getAllTasks() {
        logger.info("Fetching all tasks");
        return ResponseEntity.ok(taskService.findAllTasks());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        logger.info("Fetching task with id {}", id);
        return taskService.findTaskById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Task with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        logger.info("Creating new task: {}", task.getTitle());
        return ResponseEntity.ok(taskService.saveTask(task));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        logger.info("Updating task with id {}", id);
        task.setId(id);
        return ResponseEntity.ok(taskService.saveTask(task));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        logger.info("Deleting task with id {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}