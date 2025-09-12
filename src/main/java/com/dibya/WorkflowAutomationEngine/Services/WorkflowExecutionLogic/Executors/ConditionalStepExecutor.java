package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors;

import com.dibya.WorkflowAutomationEngine.Util.ConditionalEvaluator;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.ConditionalStepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.ConcreteListners.LoggingListeners;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.Listener;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepExecutorFactory;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepResult;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.WorkflowContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("COND")
public class ConditionalStepExecutor implements StepExecutor{

    @Autowired
    private StepExecutorFactory stepExecutorFactory;


    @Override
    public StepResult execute(WorkflowContext workflowContext, StepContext stepContext, Listener listener) {

        LoggingListeners loggingListeners = (LoggingListeners) listener;

        loggingListeners.onStepStart(stepContext.getStepId().toString(),workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()));

        if(!(stepContext instanceof ConditionalStepContext conditionalStepContext)){
            loggingListeners.onStepFailure(workflowContext.getCurrentStepId(),workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()), "Invalid step context for ConditionalStepExecutor");
            return StepResult.builder().isSuccess(false).message("Invalid step context for ConditionalStepExecutor").build();
        }

        ConditionalEvaluator conditionalEvaluator = new ConditionalEvaluator(conditionalStepContext,workflowContext);
        try {
            if (conditionalEvaluator.evaluate()) {
                WorkflowContext subContext = new WorkflowContext();
                subContext.setWorkflowId(workflowContext.getWorkflowId());
                subContext.setExecutionId(workflowContext.getExecutionId());
                subContext.getVariables().putAll(workflowContext.getVariables());
                StepContext trueStepContext = conditionalStepContext.getTrueStepContext();
                subContext.setCurrentStepId(trueStepContext.getStepId().toString());
                StepExecutor trueStepExecutor = stepExecutorFactory.getStepExecutor(trueStepContext.getStepType());
                ChainNode chainNodeTrueStep = new ChainNode(trueStepExecutor,trueStepContext,trueStepContext.getStepId());
                chainNodeTrueStep.run(subContext, listener);
                workflowContext.getVariables().putAll(subContext.getVariables());
            } else {
                WorkflowContext subContext = new WorkflowContext();
                subContext.setWorkflowId(workflowContext.getWorkflowId());
                subContext.setExecutionId(workflowContext.getExecutionId());
                subContext.getVariables().putAll(workflowContext.getVariables());

                StepContext falseStepContext = conditionalStepContext.getFalseStepContext();
                subContext.setCurrentStepId(falseStepContext.getStepId().toString());
                StepExecutor falseStepExecutor = stepExecutorFactory.getStepExecutor(falseStepContext.getStepType());
                ChainNode falseChainNode = new ChainNode(falseStepExecutor,falseStepContext,falseStepContext.getStepId());
                falseChainNode.run(subContext, listener);
                workflowContext.getVariables().putAll(subContext.getVariables());
            }
        }catch (Exception e){
            loggingListeners.onStepFailure(workflowContext.getCurrentStepId(),workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()), e.getMessage());
            return StepResult.builder().isSuccess(false).message(e.getMessage()).build();
        }
        loggingListeners.onStepEnd(workflowContext.getCurrentStepId(),workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()));
        return StepResult.builder().isSuccess(true).message("Successfully Executed").build();
    }
}
