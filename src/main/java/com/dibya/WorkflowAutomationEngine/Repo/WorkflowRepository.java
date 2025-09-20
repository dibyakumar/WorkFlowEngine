package com.dibya.WorkflowAutomationEngine.Repo;


import com.dibya.WorkflowAutomationEngine.Entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkflowRepository extends JpaRepository<Workflow,Long> {
    @Query("SELECT w FROM Workflow w WHERE w.createdBy.userName = :currentLoggedInUser")
    List<Workflow> findByUserCreated(String currentLoggedInUser);
}
