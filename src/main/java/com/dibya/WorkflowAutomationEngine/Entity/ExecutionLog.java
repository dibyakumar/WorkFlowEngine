package com.dibya.WorkflowAutomationEngine.Entity;

import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepResult;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.GenerationType.*;

@Entity
@Table(name="execution_log")
@Data
public class ExecutionLog {
     @Id
     @GeneratedValue(strategy = IDENTITY)
     private Long id;
 
     @Column(name = "execution_id", columnDefinition = "uuid", nullable = false)
     private UUID executionId;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "workflow_id", insertable = true, updatable = false)
     private Workflow workflowId;

     @ManyToOne
     @JoinColumn(name = "step_id", nullable = false)
     private Step step;
 
     @Column(name = "status", length = 20)
     private String status;
 
     @Column(name = "description")
     private String description;
 
     @Column(name = "input", columnDefinition = "jsonb")
     @Type(JsonType.class)  // from hibernate-types library
     private StepContext input;
 
     @Column(name = "output", columnDefinition = "jsonb")
     @Type(JsonType.class)  // from hibernate-types library
     private StepResult output;
 
     @Column(name = "started_at")
     private LocalDateTime  startedAt;
 
     @Column(name = "ended_at")
     private LocalDateTime endedAt;
}
