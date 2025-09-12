package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners;

import java.util.UUID;

public interface StepListener extends Listener{
    public void onStepStart(String stepId, String workflowId, UUID executionId);
    public void onStepEnd(String stepId, String workflowId, UUID executionId);
    public void onStepFailure(String stepId, String workflowId, UUID executionId, String message);
}
