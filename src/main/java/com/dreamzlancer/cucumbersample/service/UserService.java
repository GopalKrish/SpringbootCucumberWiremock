package com.dreamzlancer.cucumbersample.service;

import com.dreamzlancer.cucumbersample.model.User;
import com.dreamzlancer.cucumbersample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        List<User> users = userRepository.findAll();
        log.debug("Found {} users", users.size());
        return users;
    }

    public Optional<User> getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            log.debug("User found: {}", user.get());
        } else {
            log.warn("User with ID {} not found", id);
        }
        return user;
    }

    public User createUser(User user) {
        log.info("Creating new user: {}", user.getEmail());
        try {
            User savedUser = userRepository.save(user);
            log.info("User created successfully with ID: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<User> updateUser(Long id, User userDetails) {
        log.info("Updating user with ID: {}", id);
        return userRepository.findById(id)
                .map(existingUser -> {
                    log.debug("Existing user found: {}", existingUser);
                    existingUser.setName(userDetails.getName());
                    existingUser.setEmail(userDetails.getEmail());
                    User updatedUser = userRepository.save(existingUser);
                    log.info("User updated successfully: {}", updatedUser);
                    return updatedUser;
                })
                .or(() -> {
                    log.warn("User with ID {} not found for update", id);
                    return Optional.empty();
                });
    }

    public boolean deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("User deleted successfully");
            return true;
        }
        log.warn("User with ID {} not found for deletion", id);
        return false;
    }

    // Additional utility methods
    public long countUsers() {
        return userRepository.count();
    }

    public void deleteAllUsers() {
        log.info("Deleting all users");
        userRepository.deleteAll();
        log.info("All users deleted");
    }
}