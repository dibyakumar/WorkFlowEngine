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
    public void initializeWorkflow(WorkflowRequest workflowRequest){

        // create a new workflow entry in DB
        Workflow workflow = new Workflow();
        workflow.setStatus("A");
        workflow.setName(workflowRequest.getWorkflowName());
        String userNameFromToken = utility.getUserNameFromToken();
        User user = userRepository.findByUserName(userNameFromToken);
        workflow.setCreatedBy(user);
        workflow.setCreatedAt(LocalDate.now());
        Workflow workflowSaved = workflowRepository.save(workflow);

        List<Step> allSteps = new ArrayList<>();
        // save all steps in DB
        for(StepContext stepContext: workflowRequest.getSteps()){
            Step stepFromStepContext = utility.getStepFromStepContext(stepContext, workflowSaved);
            log.info(stepFromStepContext.getConfig().getClass().getName());
            allSteps.add(stepRepository.save(stepFromStepContext));
        }
        allSteps = utility.filterSubSteps(allSteps);
        // create step chain
        for(int i=0; i<allSteps.size()-1; i++){
            Step currentStep = allSteps.get(i);
            currentStep.setNextStep(allSteps.get(i+1));
            stepRepository.save(currentStep);
        }

    }

    public void startWorkflow(WorkflowStartRequest workflowStartRequest){
        Long workflowId = workflowStartRequest.getWorkflowId();
        if(null == workflowId)
            throw new ServiceException("Workflow ID is required to start a workflow",400);

        Workflow workflow = workflowRepository.findById(workflowId).orElse(null);

        if(null == workflow){
            throw new RuntimeException("Workflow not found with id: " + workflowId);
        }
        List<Step> allSteps = stepRepository.findByWorkflowId(workflow);
        allSteps = utility.filterSubSteps(allSteps);
        if(null == allSteps || allSteps.isEmpty()){
            throw new RuntimeException("No steps found for workflow with id: " + workflowId);
        }

        // create Chain of Responsibility
        List<ChainNode> allChainNodes = createStepChain(allSteps, workflowStartRequest.getInput());

        Step firstStep = utility.findRootStep(allSteps);
        ChainNode firstChainNode = null;
        for(ChainNode chainNode : allChainNodes){
            if(chainNode.getStepId().equals(firstStep.getId())){
                firstChainNode = chainNode;
                break;
            }
        }

        if(null == firstStep || null == firstChainNode){
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
            if(stepRes.isSuccess())
                loggingListeners.onWorkflowEnd(workflowId.toString(), executionId);
            else
                loggingListeners.onWorkflowFailure(workflowId.toString(), executionId, stepRes.getMessage());
        }catch (Exception e){
            loggingListeners.onWorkflowFailure(workflowId.toString(), executionId, e.getMessage());
        }

    }

    private List<ChainNode> createStepChain(List<Step> allSteps, Map<String,Object> input){
        List<ChainNode> allNodeExecutors = new ArrayList<>();
        // chain all steps
        for(Step step : allSteps){
            StepExecutor stepExecutor = stepExecutorFactory.getStepExecutor(step.getType());
            ChainNode chainNode = new ChainNode(stepExecutor, utility.stepToStepContextMapper(step,input),step.getId());
            allNodeExecutors.add(chainNode);
            ChainNode nextNode = null;
            if(null != step.getNextStep()) {
                StepExecutor nextStepExecutor = stepExecutorFactory.getStepExecutor(step.getNextStep().getType());
                nextNode = new ChainNode(nextStepExecutor, utility.stepToStepContextMapper(step.getNextStep(),input), step.getNextStep().getId());
                chainNode.setNextExecutorNode(nextNode);
                allNodeExecutors.add(nextNode);
            }
        }
        return allNodeExecutors;

    }
}
