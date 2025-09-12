package com.dibya.WorkflowAutomationEngine.Services.WorkflowExecutionLogic.Executors.Context;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "stepType" // JSON must include this field
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WebhookStepContext.class, name = "WEBHOOK"),
        @JsonSubTypes.Type(value = ConditionalStepContext.class, name = "COND"),
        @JsonSubTypes.Type(value = EmailStepContext.class, name = "EMAIL")
})
public interface StepContext {
    public String getStepType();
    public Long getStepId();
}
