package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic;

import com.dibya.WorkflowAutomationEngine.Entity.Workflow;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WorkflowContext {
    private String workflowId;
    private String executionId;

    // Input + dynamic variables (used for substitution)
    private final Map<String, Object> variables = new HashMap<>();

    // Current step tracking
    private String currentStepId;

}
