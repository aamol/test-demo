package com.example.javadevopsdemo.service;

import com.example.javadevopsdemo.model.Task;
import com.example.javadevopsdemo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }
    
    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    public Task saveTask(Task task) {
        if (task.getId() != null) {
            // This is an update operation
            task.setUpdatedAt(LocalDateTime.now());
        }
        return taskRepository.save(task);
    }
    
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    
    public List<Task> findCompletedTasks() {
        return taskRepository.findByCompleted(true);
    }
    
    public List<Task> findPendingTasks() {
        return taskRepository.findByCompleted(false);
    }
    
    public List<Task> searchTasks(String keyword) {
        return taskRepository.findByTitleContaining(keyword);
    }
}