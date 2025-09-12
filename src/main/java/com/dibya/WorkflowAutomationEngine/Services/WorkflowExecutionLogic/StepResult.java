package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepResult {
    private boolean isSuccess;
    private String message;
}
