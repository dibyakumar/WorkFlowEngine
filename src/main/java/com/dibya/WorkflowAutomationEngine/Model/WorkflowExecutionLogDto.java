package com.dibya.WorkflowAutomationEngine.Model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WorkflowExecutionLogDto implements LogOutputDto{
    private String workflowId;
    private String executionId;
    private String status;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
