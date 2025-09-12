package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors;

import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.Listener;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepResult;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.WorkflowContext;

public interface StepExecutor {
    public abstract StepResult execute(WorkflowContext workflowContext, StepContext stepContext, Listener listener);
}
