package com.dibya.WorkflowAutomationEngine.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowDto {
    private Long id;
    private String workflowName;
    private String workflowStatus;
    private String createdBy;
    private LocalDate createdAt;
}
