package com.dibya.WorkflowAutomationEngine.Util;

import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.ConditionalStepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.WorkflowContext;

public class ConditionalEvaluator {
    private final String condition;
    private final String leftValue;
    private final String rightValue;

    private final WorkflowContext workflowContext ;

    public ConditionalEvaluator(ConditionalStepContext context, WorkflowContext workflowContext) {
        this.condition = context.getCondition();
        this.leftValue = context.getLeftOperand();
        this.rightValue = context.getRightOperand();
        this.workflowContext = workflowContext;
    }

    public boolean evaluate() {
        if (leftValue == null || rightValue == null) {
            return false; // Handle missing values
        }

        // for boolean cases
        if("true".equalsIgnoreCase(rightValue) || "false".equalsIgnoreCase(rightValue)){
            return workflowContext.getVariables().get(this.leftValue).equals(Boolean.valueOf(rightValue));
        }

        if(rightValue.matches("^[a-zA-Z]+$")){
            return ((String)workflowContext.getVariables().get(this.leftValue)).equalsIgnoreCase(rightValue);
        }

        Integer leftValue =  (Integer) workflowContext.getVariables().get(this.leftValue);

        return switch (condition) {
            case "=" -> leftValue.equals(Integer.parseInt(rightValue));
            case "<>" -> !leftValue.equals(Integer.parseInt(rightValue));
            case ">" -> leftValue > Integer.parseInt(rightValue);
            case "<" -> leftValue < Integer.parseInt(rightValue);
            default -> throw new IllegalArgumentException("Unknown condition: " + condition);
        };
    }

}
