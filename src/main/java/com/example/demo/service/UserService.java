package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for business logic related to User entities
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieve all users from the database
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Find a specific user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Save a new or existing user
     */
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Delete a user by ID
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}