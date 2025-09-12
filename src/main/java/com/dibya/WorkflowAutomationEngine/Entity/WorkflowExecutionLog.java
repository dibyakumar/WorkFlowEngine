package com.dibya.WorkflowAutomationEngine.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "workflow_execution")
public class WorkflowExecutionLog {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        @Column(name = "workflow_id", nullable = false)
        private Long workflowId;
    
        @Column(name = "status", nullable = false, length = 20)
        private String status;
    
        @Column(name = "error_message")
        private String errorMessage;

        @Column(name= "execution_id", nullable = false, unique = true)
        private UUID executionId;
    
        @Column(name = "started_at")
        private LocalDateTime startedAt;
    
        @Column(name = "ended_at")
        private LocalDateTime endedAt;
}
