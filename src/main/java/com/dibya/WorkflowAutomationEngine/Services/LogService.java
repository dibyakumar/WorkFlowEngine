package com.dibya.WorkflowAutomationEngine.Services;

import com.dibya.WorkflowAutomationEngine.Entity.ExecutionLog;
import com.dibya.WorkflowAutomationEngine.Entity.Workflow;
import com.dibya.WorkflowAutomationEngine.Entity.WorkflowExecutionLog;
import com.dibya.WorkflowAutomationEngine.Exception.ServiceException;
import com.dibya.WorkflowAutomationEngine.Model.LogOutputDto;
import com.dibya.WorkflowAutomationEngine.Model.StepExecutionLog;
import com.dibya.WorkflowAutomationEngine.Model.WorkflowExecutionLogDto;
import com.dibya.WorkflowAutomationEngine.Repo.ExecutionLogRepository;
import com.dibya.WorkflowAutomationEngine.Repo.StepRepository;
import com.dibya.WorkflowAutomationEngine.Repo.WorkflowExecutionLogRepository;
import com.dibya.WorkflowAutomationEngine.Repo.WorkflowRepository;
import com.dibya.WorkflowAutomationEngine.Util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LogService {
    @Autowired
    private WorkflowExecutionLogRepository workflowExecutionLogRepository;
    @Autowired
    private ExecutionLogRepository executionLogRepository;
    @Autowired
    private StepRepository stepRepository;
    @Autowired
    private Utility utility;
    @Autowired
    private WorkflowRepository workflowRepository;

    public List<LogOutputDto> getLogsByTypeAndWorkflowId(String type, String workflowId,Integer page, Integer size) {
        String currentLoggedInUser = utility.getCurrentLoggedInUser();
        Optional<Workflow> workflowById = workflowRepository.findById(Long.valueOf(workflowId));
        if(workflowById.isEmpty() || !workflowById.get().getCreatedBy().getUserName().equals(currentLoggedInUser)){
            throw new ServiceException("No workflow found with the given id for the current user",404);
        }
        List<LogOutputDto> list = new ArrayList<>();
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        if("workflow".equalsIgnoreCase(type)){
            Page<WorkflowExecutionLog> byWorkflowId = workflowExecutionLogRepository.findByWorkflowId(workflowId,pageable);
            for(WorkflowExecutionLog log : byWorkflowId){
                list.add(WorkflowExecutionLogDto.builder().executionId(log.getExecutionId().toString()).status(log.getStatus()).workflowId(log.getWorkflowId().toString()).endTime(log.getEndedAt()).startTime(log.getStartedAt()).message(log.getErrorMessage()).build());
            }
            return list;
        }else if("step".equalsIgnoreCase(type)){
            Page<ExecutionLog> byWorkflowId = executionLogRepository.findByWorkflowId(workflowId,pageable);
            for(ExecutionLog log : byWorkflowId){
                list.add(StepExecutionLog.builder().executionId(log.getExecutionId().toString()).stepId(log.getStep().getId().toString()).stepName(log.getStep().getType()).status(log.getStatus()).startTime(log.getStartedAt().toString()).endTime(log.getEndedAt().toString()).message(log.getDescription()).input(log.getInput().toString()).workflowId(log.getWorkflowId().getId().toString()).nextStepId(log.getStep().getNextStep().getId().toString()).build());
            }
            return list;
        }
        return null;
    }
}
