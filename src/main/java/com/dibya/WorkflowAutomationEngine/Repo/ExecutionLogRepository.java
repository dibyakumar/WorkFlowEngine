package com.dibya.WorkflowAutomationEngine.Repo;

import com.dibya.WorkflowAutomationEngine.Entity.ExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ExecutionLogRepository extends JpaRepository<ExecutionLog,Long> {
    ExecutionLog findByExecutionId(UUID executionId);

    @Query("SELECT e FROM ExecutionLog e WHERE e.workflowId.id = ?1 order by e.endedAt desc")
    Page<ExecutionLog> findByWorkflowId(String workflowId, Pageable pageable);

    @Query("SELECT e FROM ExecutionLog e WHERE  e.executionId = ?1 AND e.step.id = ?2")
    ExecutionLog findByExecutionIdAndStepId(UUID executionId, String stepId);
}
