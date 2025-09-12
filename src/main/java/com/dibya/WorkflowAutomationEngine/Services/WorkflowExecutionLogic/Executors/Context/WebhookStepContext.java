package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebhookStepContext implements StepContext {
    private String url;
    private String method;
    private Map<String,Object> payload;
    private Map<String, String> headers;
    private Long stepId;
    private Map<String, String> responseMappings;
    private String stepType;

    @Override
    public String getStepType() {
        return "WEBHOOK";
    }

}
