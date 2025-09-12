package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors;

import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.ConcreteListners.LoggingListeners;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.Listener;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepExecutorFactory;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepResult;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.WorkflowContext;
import lombok.Data;

import java.util.UUID;

@Data
public class ChainNode {
    private StepExecutor stepExecutor;
    private StepContext stepContext;
    private Long stepId;
    private ChainNode nextExecutorNode;

    public ChainNode(StepExecutor stepExecutor, StepContext stepContext, Long stepId) {
        this.stepExecutor = stepExecutor;
        this.stepContext = stepContext;
        this.stepId = stepId;
        this.nextExecutorNode = null;
    }


    public StepResult run(WorkflowContext workflowContext, Listener listener){
        StepResult stepResult = stepExecutor.execute(workflowContext, stepContext, listener);
        if(!stepResult.isSuccess() ){
            return stepResult;
        }
        if(nextExecutorNode != null){
            workflowContext.setCurrentStepId(nextExecutorNode.getStepId().toString());
            return nextExecutorNode.run(workflowContext, listener);
        }
        return stepResult;
    }


}
