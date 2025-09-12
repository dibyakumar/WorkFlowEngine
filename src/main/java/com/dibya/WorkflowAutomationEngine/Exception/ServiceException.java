package com.dibya.WorkflowAutomationEngine.Exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends  RuntimeException{
    private String message;
    private int statusCode;

    public ServiceException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
