package com.example.javadevopsdemo.repository;

import com.example.javadevopsdemo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Find tasks by completion status
    List<Task> findByCompleted(boolean completed);
    
    // Find tasks containing a title keyword
    List<Task> findByTitleContaining(String keyword);
}