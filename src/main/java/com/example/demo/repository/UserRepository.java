package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity
 * Spring Data JPA automatically provides implementation
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be added here if needed
}