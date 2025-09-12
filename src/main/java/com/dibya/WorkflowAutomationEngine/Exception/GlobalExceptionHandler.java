package com.dibya.WorkflowAutomationEngine.Exception;

import com.dibya.WorkflowAutomationEngine.Model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Configuration
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> handleServiceException(ServiceException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(new ErrorResponse(ex.getMessage(), ex.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        log.error(ex.getMessage());
        // Return a generic error response
        return ResponseEntity.status(500)
                .body(new ErrorResponse(ex.getMessage(), 500));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleGenericException(RuntimeException ex) {
        log.error(ex.getMessage());
        // Return a generic error response
        return ResponseEntity.status(500)
                .body(new ErrorResponse(ex.getMessage(), 500));
    }

}
