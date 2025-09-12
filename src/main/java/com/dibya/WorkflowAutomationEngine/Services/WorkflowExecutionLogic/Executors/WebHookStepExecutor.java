package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors;

import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.StepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context.WebhookStepContext;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.ConcreteListners.LoggingListeners;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Listeners.Listener;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepExecutorFactory;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.StepResult;
import com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.WorkflowContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
@Component("WEBHOOK")
public class WebHookStepExecutor implements StepExecutor{

    @Override
    public StepResult execute(WorkflowContext workflowContext, StepContext stepContext, Listener listener) {
        LoggingListeners loggingListeners = (LoggingListeners) listener;

        loggingListeners.onStepStart(stepContext.getStepId().toString(),workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()));
        if(!(stepContext instanceof WebhookStepContext webhookStepContext)){
            loggingListeners.onStepFailure(workflowContext.getCurrentStepId(), workflowContext.getWorkflowId(), null, "Invalid step context for WebHookStepExecutor");
            return StepResult.builder().isSuccess(false).message("Invalid step context for WebHookStepExecutor").build();
        }

        HttpHeaders headers = new HttpHeaders();
        if (webhookStepContext.getHeaders() != null) {
            for (Map.Entry<String, String> entry : webhookStepContext.getHeaders().entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String payloadAsString = objectMapper.writeValueAsString(((WebhookStepContext) stepContext).getPayload());
            HttpEntity<String> entity = new HttpEntity<>(payloadAsString, headers);

            RestTemplate restTemplate = new RestTemplate();

        // Execute webhook
        ResponseEntity<String> response = restTemplate.exchange(
                webhookStepContext.getUrl(),
                HttpMethod.valueOf(webhookStepContext.getMethod().toUpperCase()),
                entity,
                String.class
        );


            Map<String, Object> bodyMap = objectMapper.readValue(response.getBody(), Map.class);

            // Store required fields into runtime context
            if (webhookStepContext.getResponseMappings() != null) {
                for (Map.Entry<String, String> entry : webhookStepContext.getResponseMappings().entrySet()) {
                    String responseField = entry.getKey();     // field in JSON
                    String contextKey = entry.getValue();      // key in runtime context
                    if (bodyMap.containsKey(responseField)) {
                        workflowContext.getVariables().put(contextKey, bodyMap.get(responseField));
                    }
                }
            }

        } catch (Exception e) {
            loggingListeners.onStepFailure(stepContext.getStepId().toString(), workflowContext.getWorkflowId(), UUID.fromString(workflowContext.getExecutionId()), "Failed to parse webhook response: " + e.getMessage());
            return StepResult.builder().isSuccess(false).message("Failed to execute webhook: " + e.getMessage()).build();
        }

        loggingListeners.onStepEnd(stepContext.getStepId().toString(),workflowContext.getWorkflowId(),UUID.fromString(workflowContext.getExecutionId()));
        return StepResult.builder().isSuccess(true).message("Successfully Executed").build();
    }
}





