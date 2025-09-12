package com.dibya.WorkflowAutomationEngine.Entity;

import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name="step")
@Data
public class Step {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    @JoinColumn(name = "workflow_id",  updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Workflow workflowId;
    @JoinColumn(name = "next_step_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Step nextStep; // self-referencing foreign key
    @Type(JsonType.class)  // from hibernate-types library
    @Column(columnDefinition = "jsonb")
    private StepContext config;

    @JoinColumn(name = "true_step_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Step trueStep;

    @JoinColumn(name="false_step_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Step falseStep;

    @Column(name = "creation_date", nullable = false)
    private LocalDate createdAt;
}
