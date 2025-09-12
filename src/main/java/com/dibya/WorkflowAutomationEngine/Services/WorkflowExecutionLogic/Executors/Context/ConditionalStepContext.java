package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConditionalStepContext implements StepContext{
    private String condition;
    private String leftOperand;
    private String rightOperand;
    private Long stepId;
    private String stepType;

    private  StepContext trueStepContext;
    private  StepContext falseStepContext;


    @Override
    public String getStepType() {
        return "COND";
    }
}
