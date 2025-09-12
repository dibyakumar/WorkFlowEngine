package com.dibya.WorkflowAutomationEngine.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "app_user")
@Data
public class User {
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name="id")
    @Id
    private Long id;
    @Column(name="name", nullable = false)
    private String userName;
    @Column(name="password", nullable = false)
    private String password;
    @Column(name="email", nullable = false, unique = true)
    private String email;
    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;
}
