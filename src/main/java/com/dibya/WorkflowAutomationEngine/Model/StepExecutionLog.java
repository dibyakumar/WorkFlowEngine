package com.dibya.WorkflowAutomationEngine.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepExecutionLog implements  LogOutputDto{
    private String stepId;
    private String stepName;
    private String status;
    private String message;
    private String startTime;
    private String endTime;
    private String workflowId;
    private String executionId;
    private String input;
    private String nextStepId;
}
