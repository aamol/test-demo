package com.example.aidevopsdemo.service;

import com.example.aidevopsdemo.model.Task;
import com.example.aidevopsdemo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for Task-related business logic
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Find all tasks in the system
     */
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Find a task by its ID
     */
    public Optional<Task> findTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Save a new task
     */
    public Task createTask(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    /**
     * Update an existing task
     */
    public Optional<Task> updateTask(Long id, Task taskDetails) {
        return taskRepository.findById(id).map(existingTask -> {
            existingTask.setTitle(taskDetails.getTitle());
            existingTask.setDescription(taskDetails.getDescription());
            existingTask.setCompleted(taskDetails.isCompleted());
            return taskRepository.save(existingTask);
        });
    }

    /**
     * Delete a task by its ID
     */
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    /**
     * Find tasks by completion status
     */
    public List<Task> findTasksByCompletionStatus(boolean completed) {
        return taskRepository.findByCompleted(completed);
    }
}