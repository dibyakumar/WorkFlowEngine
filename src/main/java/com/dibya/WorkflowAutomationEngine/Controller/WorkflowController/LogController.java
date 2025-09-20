package com.dibya.WorkflowAutomationEngine.Controller.WorkflowController;

import com.dibya.WorkflowAutomationEngine.Services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/{type}")
    public ResponseEntity<?> getLogs(@PathVariable String type, @RequestParam String workflowId,@RequestParam Integer page,@RequestParam Integer size) {

        return ResponseEntity.ok().body(logService.getLogsByTypeAndWorkflowId(type, workflowId,page,size));
    }

}
