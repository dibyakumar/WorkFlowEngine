package com.dibya.WorkflowAutomationEngine.Model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TokenResponse {
    private String token;
    private String refreshToken;
    private LocalDateTime creationDate;
}
