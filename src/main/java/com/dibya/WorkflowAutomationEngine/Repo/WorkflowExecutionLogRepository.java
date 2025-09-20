package com.dibya.WorkflowAutomationEngine.Repo;

import com.dibya.WorkflowAutomationEngine.Entity.WorkflowExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface WorkflowExecutionLogRepository extends JpaRepository<WorkflowExecutionLog,Long> {
    WorkflowExecutionLog findByExecutionId(UUID executionId);

    @Query("SELECT w FROM WorkflowExecutionLog w WHERE w.workflowId = ?1 order by w.endedAt desc")
    Page<WorkflowExecutionLog> findByWorkflowId(String workflowId, Pageable pageable);
}
