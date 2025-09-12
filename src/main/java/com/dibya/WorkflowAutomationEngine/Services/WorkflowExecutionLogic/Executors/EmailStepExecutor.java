package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors;

import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.EmailStepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.ConcreteListners.LoggingListeners;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.Listener;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepExecutorFactory;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepResult;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.WorkflowContext;
import com.dibya.WorkflowAutomationEngine.Util.EmailService;
import com.dibya.WorkflowAutomationEngine.Util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("EMAIL")
public class EmailStepExecutor implements StepExecutor{
    @Autowired
    private  EmailService emailService;

    @Autowired
    private  Utility utility;

    @Override
    public StepResult execute(WorkflowContext workflowContext, StepContext stepContext, Listener listener) {
        LoggingListeners loggingListeners = (LoggingListeners) listener;
        loggingListeners.onStepStart(stepContext.getStepId().toString(), workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()));
        if(!(stepContext instanceof EmailStepContext emailStepContext)){
            loggingListeners.onStepFailure(workflowContext.getCurrentStepId(), workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()), "Invalid step context for EmailStepExecutor");
            return StepResult.builder().isSuccess(false).message("Invalid step context for EmailStepExecutor").build();
        }
        try {
            emailService.sendEmail(emailStepContext.getEmailAddress(), emailStepContext.getSubject(), utility.getEmailBody(emailStepContext.getBody()));
        }catch (Exception e){
            loggingListeners.onStepFailure(workflowContext.getCurrentStepId(), workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()), e.getMessage());
            return StepResult.builder().isSuccess(false).message(e.getMessage()).build();
        }
        loggingListeners.onStepEnd(emailStepContext.getStepId().toString(), workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()));

        return StepResult.builder().isSuccess(true).message("Successfully Executed").build();
    }
}
