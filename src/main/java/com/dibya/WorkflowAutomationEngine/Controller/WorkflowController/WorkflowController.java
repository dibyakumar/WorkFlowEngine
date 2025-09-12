package com.dibya.WorkflowAutomationEngine.Controller.WorkflowController;

import com.dibya.WorkflowAutomationEngine.Model.WorkflowRequest;
import com.dibya.WorkflowAutomationEngine.Model.WorkflowStartRequest;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.WorkflowEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowEngine workflowEngine;

    @PostMapping("/create")
    public ResponseEntity<?> createWorkflow(@RequestBody WorkflowRequest workflowRequest) {
        workflowEngine.initializeWorkflow(workflowRequest);
        return ResponseEntity.ok("Workflow Created Successfully");
    }

    @PostMapping("/start")
    public ResponseEntity<?> startWorkflow(@RequestBody WorkflowStartRequest workflowStartRequest) {
        workflowEngine.startWorkflow(workflowStartRequest);
        return ResponseEntity.ok("Workflow Started Successfully");
    }
}
