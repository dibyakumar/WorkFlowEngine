package com.dibya.WorkflowAutomationEngine.Model;

import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepDto {
    private Long id;
    private String stepType;
    private String nextStepId;
    private StepContext stepContext;
}
