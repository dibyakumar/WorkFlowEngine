package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners;

import java.util.UUID;

public interface WorkflowListener extends Listener{
   public void onWorkflowStart(String workflowId, UUID executionId);

   public void onWorkflowEnd(String workflowId, UUID executionId);

   public void onWorkflowFailure(String workflowId, UUID executionId, String message);
}
