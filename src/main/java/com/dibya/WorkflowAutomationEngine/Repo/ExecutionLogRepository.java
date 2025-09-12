package com.dibya.WorkflowAutomationEngine.Repo;

import com.dibya.WorkflowAutomationEngine.Entity.ExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExecutionLogRepository extends JpaRepository<ExecutionLog,Long> {
    ExecutionLog findByExecutionId(UUID executionId);
}
