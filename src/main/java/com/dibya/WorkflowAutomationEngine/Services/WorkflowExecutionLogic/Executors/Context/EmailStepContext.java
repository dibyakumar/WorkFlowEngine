package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailStepContext implements StepContext{
    private String emailAddress;
    private String subject;
    private String body;
    private Long stepId;
    private String stepType;

    @Override
    public String getStepType() {
        return "EMAIL";
    }



}
