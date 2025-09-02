package com.dreamzlancer.cucumbersample.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // You can add more fields as needed
    /*
    @Column(length = 20)
    private String phone;

    @Column
    private Integer age;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
    */

    // Constructor without id for creation
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}