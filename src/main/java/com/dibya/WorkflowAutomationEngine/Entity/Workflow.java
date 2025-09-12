package com.dibya.WorkflowAutomationEngine.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name="workflow")
@Data
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="name", nullable = false)
    private String name;
    @Column(name="status")
    private String status; // e.g., "A", "I"
    @JoinColumn(name="created_by", nullable = false)
    @ManyToOne
    private User createdBy; // User ID or username
    @Column(name="date_of_creation", nullable = false)
    private LocalDate createdAt;
}
