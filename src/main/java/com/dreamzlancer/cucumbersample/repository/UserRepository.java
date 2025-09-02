package com.dreamzlancer.cucumbersample.repository;

import com.dreamzlancer.cucumbersample.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods can be added here
    // Spring Data JPA will automatically implement them

    // Example: Find by email
    // Optional<User> findByEmail(String email);

    // Example: Find by name containing ignore case
    // List<User> findByNameContainingIgnoreCase(String name);
}