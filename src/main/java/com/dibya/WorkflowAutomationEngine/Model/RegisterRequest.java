package com.dibya.WorkflowAutomationEngine.Model;

import lombok.Data;

@Data
public class RegisterRequest {
    public String username;
    public String password;
    public String email;
    public String confirmPassword;
}
