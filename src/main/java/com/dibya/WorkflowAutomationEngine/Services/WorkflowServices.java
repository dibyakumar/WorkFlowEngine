package com.dibya.WorkflowAutomationEngine.Services;

import com.dibya.WorkflowAutomationEngine.Entity.Step;
import com.dibya.WorkflowAutomationEngine.Entity.Workflow;
import com.dibya.WorkflowAutomationEngine.Exception.ServiceException;
import com.dibya.WorkflowAutomationEngine.Model.StepDto;
import com.dibya.WorkflowAutomationEngine.Model.WorkflowDto;
import com.dibya.WorkflowAutomationEngine.Repo.StepRepository;
import com.dibya.WorkflowAutomationEngine.Repo.WorkflowRepository;
import com.dibya.WorkflowAutomationEngine.Util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WorkflowServices {
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private Utility utility;
    @Autowired
    private StepRepository stepRepository;

    public List<WorkflowDto> getAllWorkflows() {
        List<Workflow> workflows = workflowRepository.findByUserCreated(utility.getCurrentLoggedInUser());
        List<WorkflowDto> workflowDtos = new ArrayList<>();
        for (Workflow workflow : workflows) {
            workflowDtos.add(WorkflowDto.builder().workflowName(workflow.getName()).workflowStatus(workflow.getStatus()).createdAt(workflow.getCreatedAt()).id(workflow.getId()).build());
        }
        return workflowDtos;
    }

    public List<StepDto> getStepsOfWorkflow(Long workflowId) {
        List<Workflow> workflows = workflowRepository.findByUserCreated(utility.getCurrentLoggedInUser());
        Optional<Workflow> workflowOptional = workflows.stream().filter(workflow -> workflow.getId().equals(workflowId)).findFirst();
        if (workflowOptional.isEmpty()) {
           throw new ServiceException("Workflow Id Provided Is Not present Or Not Created by Logged In User",400);
        }
        List<Step> byWorkflowId = stepRepository.findByWorkflowId(workflowOptional.get());
        List<StepDto> stepDtos = new ArrayList<>();
        for( Step step : byWorkflowId){
            stepDtos.add(StepDto.builder().stepContext(step.getConfig()).stepType(step.getType()).id(step.getId()).build());
        }
        return stepDtos;
    }
}
