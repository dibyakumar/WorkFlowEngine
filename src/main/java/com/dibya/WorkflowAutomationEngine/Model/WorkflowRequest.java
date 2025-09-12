package com.dibya.WorkflowAutomationEngine.Model;

import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import lombok.Data;
import java.util.List;
/*
    passing
    "type": "" -> is mandatory for deserialization to work
 */
@Data
public class WorkflowRequest {
    private String workflowName;
    private List<StepContext> steps;
}
