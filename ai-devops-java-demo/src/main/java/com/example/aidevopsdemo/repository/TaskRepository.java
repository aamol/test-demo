package com.example.aidevopsdemo.repository;

import com.example.aidevopsdemo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Task entity operations
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByCompleted(boolean completed);
    
    List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}