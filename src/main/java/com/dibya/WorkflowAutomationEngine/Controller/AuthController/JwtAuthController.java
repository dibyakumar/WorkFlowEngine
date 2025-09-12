package com.dibya.WorkflowAutomationEngine.Controller.AuthController;

import com.dibya.WorkflowAutomationEngine.Model.RegisterRequest;
import com.dibya.WorkflowAutomationEngine.Model.TokenRequest;
import com.dibya.WorkflowAutomationEngine.Services.AuthenticationLogic;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class JwtAuthController {
    @Autowired
    private AuthenticationLogic authenticationLogic;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        return authenticationLogic.registerUser(request);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(String token) {
        return authenticationLogic.validateToken(token);
    }

    @GetMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody TokenRequest tokenRequest) {
        return authenticationLogic.getToken(tokenRequest);
    }
}
