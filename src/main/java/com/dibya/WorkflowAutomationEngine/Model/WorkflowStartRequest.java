package com.dibya.WorkflowAutomationEngine.Model;

import lombok.Data;

import java.util.Map;

@Data
public class WorkflowStartRequest {
    private Long workflowId;
    private Map<String,Object> input;
}
