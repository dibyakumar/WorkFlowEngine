package com.dibya.WorkflowAutomationEngine.Repo;

import com.dibya.WorkflowAutomationEngine.Entity.WorkflowExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkflowExecutionLogRepository extends JpaRepository<WorkflowExecutionLog,Long> {
    WorkflowExecutionLog findByExecutionId(UUID executionId);
}
