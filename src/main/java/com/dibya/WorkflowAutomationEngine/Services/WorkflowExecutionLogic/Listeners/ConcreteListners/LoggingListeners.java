package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.ConcreteListners;

import com.dibya.WorkflowAutomationEngine.Entity.ExecutionLog;
import com.dibya.WorkflowAutomationEngine.Entity.Step;
import com.dibya.WorkflowAutomationEngine.Entity.Workflow;
import com.dibya.WorkflowAutomationEngine.Entity.WorkflowExecutionLog;
import com.dibya.WorkflowAutomationEngine.Exception.ServiceException;
import com.dibya.WorkflowAutomationEngine.Repo.ExecutionLogRepository;
import com.dibya.WorkflowAutomationEngine.Repo.StepRepository;
import com.dibya.WorkflowAutomationEngine.Repo.WorkflowExecutionLogRepository;
import com.dibya.WorkflowAutomationEngine.Repo.WorkflowRepository;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.StepListener;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.WorkflowListener;
import com.dibya.WorkflowAutomationEngine.Util.EXECUTION_STATUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class LoggingListeners implements WorkflowListener, StepListener {

    @Autowired
    private ExecutionLogRepository executionLogRepository;
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private WorkflowExecutionLogRepository workflowExecutionLogRepository;

    @Override
    public void onStepStart(String stepId, String workflowId, UUID executionId) {
            if(null == getExecutionLog(executionId)){
                ExecutionLog executionLog = new ExecutionLog();
                executionLog.setExecutionId(executionId);
                Optional<Workflow> workflow = workflowRepository.findById(Long.valueOf(workflowId));
                executionLog.setWorkflowId(workflow.orElse(null));
                executionLog.setStatus(EXECUTION_STATUS.IN_PROGRESS.name());
                executionLog.setStartedAt(LocalDateTime.now());

                Step step = stepRepository.findById(Long.valueOf(stepId)).orElse(null);
                executionLog.setStep(step);
                if(null == step)
                    throw new ServiceException("Step not found with id: " + stepId,500);
                executionLog.setInput(step.getConfig());

                executionLogRepository.save(executionLog);
            }
    }

    @Override
    public void onStepEnd(String stepId, String workflowId, UUID executionId) {
        ExecutionLog executionLog = getExecutionLog(executionId);
        if(null != executionLog){
            executionLog.setStatus(EXECUTION_STATUS.COMPLETED.name());
            executionLog.setEndedAt(LocalDateTime.now());

            Step step = stepRepository.findById(Long.valueOf(stepId)).orElse(null);
            if(null == step)
                throw new ServiceException("Step not found with id: " + stepId,500);

            executionLogRepository.save(executionLog);
        }
    }

    @Override
    public void onStepFailure(String stepId, String workflowId, UUID executionId, String message) {
        ExecutionLog executionLog = getExecutionLog(executionId);
        if(null != executionLog){
            executionLog.setStatus(EXECUTION_STATUS.FAILED.name());
            executionLog.setEndedAt(LocalDateTime.now());
            executionLog.setDescription(message);
            executionLogRepository.save(executionLog);
        }
    }

    @Override
    public void onWorkflowStart(String workflowId, UUID executionId) {
        if(null == getWorkflowExecutionLog(executionId)){
            WorkflowExecutionLog workflowExecutionLog = new WorkflowExecutionLog();
            workflowExecutionLog = new WorkflowExecutionLog();
            workflowExecutionLog.setExecutionId(executionId);
            workflowExecutionLog.setWorkflowId(Long.valueOf(workflowId));
            workflowExecutionLog.setStatus(EXECUTION_STATUS.IN_PROGRESS.name());
            workflowExecutionLog.setStartedAt(LocalDateTime.now());
            workflowExecutionLogRepository.save(workflowExecutionLog);
        }
    }

    @Override
    public void onWorkflowEnd(String workflowId, UUID executionId) {
        WorkflowExecutionLog workflowExecutionLog = getWorkflowExecutionLog(executionId);
        if(null != workflowExecutionLog){
            workflowExecutionLog.setStatus(EXECUTION_STATUS.COMPLETED.name());
            workflowExecutionLog.setEndedAt(LocalDateTime.now());
            workflowExecutionLogRepository.save(workflowExecutionLog);
        }
    }

    @Override
    public void onWorkflowFailure(String workflowId, UUID executionId, String message) {
            WorkflowExecutionLog workflowExecutionLog = getWorkflowExecutionLog(executionId);
            if(null != workflowExecutionLog){
                workflowExecutionLog.setStatus(EXECUTION_STATUS.FAILED.name());
                workflowExecutionLog.setEndedAt(LocalDateTime.now());
                workflowExecutionLog.setErrorMessage(message);
                workflowExecutionLogRepository.save(workflowExecutionLog);
            }
    }

    private ExecutionLog getExecutionLog(UUID executionId) {
        return executionLogRepository.findByExecutionId(executionId);
    }

    private WorkflowExecutionLog getWorkflowExecutionLog(UUID executionId) {
        return workflowExecutionLogRepository.findByExecutionId(executionId);
    }
}
