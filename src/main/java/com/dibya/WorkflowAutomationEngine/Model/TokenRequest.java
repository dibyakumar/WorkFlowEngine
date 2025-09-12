package com.dibya.WorkflowAutomationEngine.Model;

import lombok.Builder;
import lombok.Data;

@Data
public class TokenRequest {
    private String username;
    private String password;
}
