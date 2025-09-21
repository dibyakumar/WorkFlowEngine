package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic;

import com.dibya.WorkflowAutomationEngine.Entity.Step;
import com.dibya.WorkflowAutomationEngine.Entity.User;
import com.dibya.WorkflowAutomationEngine.Entity.Workflow;
import com.dibya.WorkflowAutomationEngine.Exception.ServiceException;
import com.dibya.WorkflowAutomationEngine.Model.WorkflowRequest;
import com.dibya.WorkflowAutomationEngine.Model.WorkflowStartRequest;
import com.dibya.WorkflowAutomationEngine.Repo.StepRepository;
import com.dibya.WorkflowAutomationEngine.Repo.UserRepository;
import com.dibya.WorkflowAutomationEngine.Repo.WorkflowRepository;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.ChainNode;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.StepExecutor;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.ConcreteListners.LoggingListeners;
import com.dibya.WorkflowAutomationEngine.Util.Utility;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkflowEngine {

    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private Utility utility;

    @Autowired
    private LoggingListeners loggingListeners;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StepExecutorFactory stepExecutorFactory;

    @Transactional
    public void initializeWorkflow(WorkflowRequest workflowRequest) {

        // create a new workflow entry in DB
        Workflow workflow = new Workflow();
        workflow.setStatus("A");
        workflow.setName(workflowRequest.getWorkflowName());
        String userNameFromToken = utility.getCurrentLoggedInUser();
        User user = userRepository.findByUserName(userNameFromToken);
        workflow.setCreatedBy(user);
        workflow.setCreatedAt(LocalDate.now());
        Workflow workflowSaved = workflowRepository.save(workflow);

        List<Step> allSteps = new ArrayList<>();
        // save all steps in DB
        for (StepContext stepContext : workflowRequest.getSteps()) {
            Step stepFromStepContext = utility.getStepFromStepContext(stepContext, workflowSaved);
            log.info(stepFromStepContext.getConfig().getClass().getName());
            allSteps.add(stepRepository.save(stepFromStepContext));
        }
        allSteps = utility.filterSubSteps(allSteps);
        // create step chain
        for (int i = 0; i < allSteps.size() - 1; i++) {
            Step currentStep = allSteps.get(i);
            currentStep.setNextStep(allSteps.get(i + 1));
            stepRepository.save(currentStep);
        }

    }

    public void startWorkflow(WorkflowStartRequest workflowStartRequest) {
        Long workflowId = workflowStartRequest.getWorkflowId();
        if (null == workflowId)
            throw new ServiceException("Workflow ID is required to start a workflow", 400);
        String currentLoggedInUser = utility.getCurrentLoggedInUser();
        Workflow workflow = workflowRepository.findById(workflowId).orElse(null);
        // validate workflow and user
        if (null == workflow || !workflow.getCreatedBy().getUserName().equals(currentLoggedInUser)) {
            throw new ServiceException("Workflow not found with id: " + workflowId,400);
        }

        if ("I".equalsIgnoreCase(workflow.getStatus()))
            throw new ServiceException("Workflow with id: " + workflowId + " is inactive", 400);

        List<Step> allSteps = stepRepository.findByWorkflowId(workflow);
        allSteps = utility.filterSubSteps(allSteps);
        if (null == allSteps || allSteps.isEmpty()) {
            throw new RuntimeException("No steps found for workflow with id: " + workflowId);
        }

        // create Chain of Responsibility
        List<ChainNode> allChainNodes = createStepChain(allSteps, workflowStartRequest.getInput());

        Step firstStep = utility.findRootStep(allSteps);
        ChainNode firstChainNode = null;
        for (ChainNode chainNode : allChainNodes) {
            if (chainNode.getStepId().equals(firstStep.getId())) {
                firstChainNode = chainNode;
                break;
            }
        }

        if (null == firstStep || null == firstChainNode) {
            throw new RuntimeException("Failed to create step chain for workflow with id: " + workflowId);
        }

        // initialize workflow context
        WorkflowContext workflowContext = new WorkflowContext();
        workflowContext.setWorkflowId(workflowId.toString());
        workflowContext.setCurrentStepId(firstStep.getId().toString());
        UUID executionId = UUID.randomUUID();
        workflowContext.setExecutionId(executionId.toString());
        workflowContext.getVariables().putAll(workflowStartRequest.getInput());

        loggingListeners.onWorkflowStart(workflowId.toString(), executionId);
        try {
            StepResult stepRes = firstChainNode.run(workflowContext, loggingListeners);
            if (stepRes.isSuccess())
                loggingListeners.onWorkflowEnd(workflowId.toString(), executionId);
            else
                loggingListeners.onWorkflowFailure(workflowId.toString(), executionId, stepRes.getMessage());
        } catch (Exception e) {
            loggingListeners.onWorkflowFailure(workflowId.toString(), executionId, e.getMessage());
        }

    }

    // create chain of responsibility for all steps using depth first search
    private List<ChainNode> createStepChain(List<Step> allSteps, Map<String, Object> input) {
        List<ChainNode> allNodeExecutors = new ArrayList<>();
        // visited map to keep track of visited steps
        Map<Long,Boolean> visited = allSteps.stream().collect(Collectors.toMap(Step::getId, step -> false));
        // chain all steps
        for (Step step : allSteps) {
           if(visited.get(step.getId()))
               continue;
           ChainNode parentNode = new ChainNode(stepExecutorFactory.getStepExecutor(step.getType()),utility.stepToStepContextMapper(step,input),step.getId());
           allNodeExecutors.add(parentNode);
           depthFirstSearch(step,visited,allNodeExecutors,parentNode,input);
        }
        return allNodeExecutors;

    }
//-1917930650
    private void depthFirstSearch(Step parentStep,Map<Long,Boolean> visited,List<ChainNode> listOfChainNodes,ChainNode parentNode,Map<String,Object> input){
        if(visited.get(parentStep.getId()))
            return;
        visited.put(parentStep.getId(),true);
        if(null == parentStep.getNextStep()){
              return;
        }
        Step nextStep = parentStep.getNextStep();
        ChainNode childNode = new ChainNode(stepExecutorFactory.getStepExecutor(nextStep.getType()), utility.stepToStepContextMapper(nextStep, input), nextStep.getId());
        listOfChainNodes.add(childNode);
        parentNode.setNextExecutorNode(childNode);
        depthFirstSearch(nextStep,visited,listOfChainNodes,childNode,input);
    }

    public void updateWorkflow(WorkflowRequest workflowRequest) {

    }

    public void toggleStatus(List<Long> workflowId) {
        if (null == workflowId)
            throw new ServiceException("Workflow ID is required to toggle status", 400);

        for (Long id : workflowId) {
                updateStatusOfWorkflow(workflowId, id);
        }
    }

    private void updateStatusOfWorkflow(List<Long> workflowId, Long id) {
        Workflow workflow = workflowRepository.findById(id).orElse(null);

        if (null == workflow) {
            throw new RuntimeException("Workflow not found with id: " + workflowId);
        }

        if ("A".equalsIgnoreCase(workflow.getStatus()))
            workflow.setStatus("I");
        else
            workflow.setStatus("A");

        workflowRepository.save(workflow);
    }
}