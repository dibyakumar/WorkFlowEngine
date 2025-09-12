package com.dibya.WorkflowAutomationEngine.Repo;

import com.dibya.WorkflowAutomationEngine.Entity.Step;
import com.dibya.WorkflowAutomationEngine.Entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<Step , Long> {
    List<Step> findByWorkflowId(Workflow workflowId);
}
