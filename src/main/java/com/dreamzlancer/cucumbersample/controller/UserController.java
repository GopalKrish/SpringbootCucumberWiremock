package com.dreamzlancer.cucumbersample.controller;

import com.dreamzlancer.cucumbersample.model.User;
import com.dreamzlancer.cucumbersample.model.UserBankDetailsResponse;
import com.dreamzlancer.cucumbersample.service.DownstreamBankService;
import com.dreamzlancer.cucumbersample.service.DownstreamServiceException;
import com.dreamzlancer.cucumbersample.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Log4j2
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Autowired
    private DownstreamBankService downstreamService;

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("GET /api/users - Fetching all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.debug("GET /api/users/{} - Fetching user by ID", id);
        return userService.getUserById(id)
                .map(user -> {
                    log.debug("User found: {}", user);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    log.warn("User with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }
    @GetMapping("/{id}/bank-details")
    public ResponseEntity<UserBankDetailsResponse> getUserBankDetailsById(@PathVariable Long id) {
        log.debug("GET /api/users/{}/bank-details - Fetching user bank details from downstream service", id);

        try {
            // Call downstream service for bank details
            UserBankDetailsResponse bankDetails = downstreamService.getUserBankDetails(id);

            log.debug("Bank details retrieved successfully for user ID: {}", id);
            return ResponseEntity.ok(bankDetails);

        } catch (DownstreamServiceException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                log.warn("User bank details not found for ID: {}", id);
                return ResponseEntity.notFound().build();
            }
            log.error("Error fetching bank details from downstream service for user ID: {}", id, ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();

        } catch (Exception ex) {
            log.error("Unexpected error fetching bank details for user ID: {}", id, ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("POST /api/users - Creating new user: {}", user.getEmail());
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        log.info("PUT /api/users/{} - Updating user", id);
        return userService.updateUser(id, user)
                .map(updatedUser -> {
                    log.info("User updated successfully: {}", updatedUser);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElseGet(() -> {
                    log.warn("User with ID {} not found for update", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deleting user", id);
        return userService.deleteUser(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}