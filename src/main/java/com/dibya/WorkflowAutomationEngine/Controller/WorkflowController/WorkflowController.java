package com.dibya.WorkflowAutomationEngine.Controller.WorkflowController;

import com.dibya.WorkflowAutomationEngine.Model.WorkflowRequest;
import com.dibya.WorkflowAutomationEngine.Model.WorkflowStartRequest;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.WorkflowEngine;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workflow")
public class WorkflowController {

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    private WorkflowServices workflowServices;

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

    @GetMapping("/workflows")
    public ResponseEntity<?> getWorkflows(){
        return ResponseEntity.ok(workflowServices.getAllWorkflows());
    }

    @PutMapping("/toggle-status")
    public ResponseEntity<?> toggleStatus(@RequestParam List<Long> workflowId){
        workflowEngine.toggleStatus(workflowId);
        return ResponseEntity.ok("Workflow status toggled successfully");
    }

    @GetMapping("/steps-by-workflowid/{workflowId}")
    public ResponseEntity<?> getStepsOfWorkflow(@PathVariable Long workflowId){
        return ResponseEntity.ok(workflowServices.getStepsOfWorkflow(workflowId));
    }

}
