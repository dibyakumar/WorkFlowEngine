package com.dibya.WorkflowAutomationEngine.Repo;


import com.dibya.WorkflowAutomationEngine.Entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<Workflow,Long> {
}
