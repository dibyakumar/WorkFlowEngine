package com.dibya.WorkflowAutomationEngine.Services;

import com.dibya.WorkflowAutomationEngine.Entity.User;
import com.dibya.WorkflowAutomationEngine.Model.RegisterRequest;
import com.dibya.WorkflowAutomationEngine.Model.TokenRequest;
import com.dibya.WorkflowAutomationEngine.Model.TokenResponse;
import com.dibya.WorkflowAutomationEngine.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class AuthenticationLogic {

    private UserRepository urepo;
    private AuthService authService;
    private PasswordEncoder endcoder;
    public AuthenticationLogic(UserRepository repo,AuthService service,PasswordEncoder endcoder) {
        this.urepo = repo;
        this.authService = service;
        this.endcoder = endcoder;
    }

    public ResponseEntity<?> registerUser(RegisterRequest request) {
        User byName = urepo.findByUserName(request.username);
        if(null != byName) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if(!request.password.equals(request.confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }
        User user = new User();
        user.setUserName(request.username);
        user.setPassword(endcoder.encode(request.password));
        user.setEmail(request.email);
        user.setCreatedAt(LocalDate.now());
        urepo.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<?> validateToken(String token) {
        if (!authService.validateJwtToken(token)) {
            return ResponseEntity.status(401).body("Invalid token");
        }
        return ResponseEntity.ok("Token is valid");
    }

    public ResponseEntity<?> getToken(TokenRequest request) {
        if(!isUserExists(request.getUsername())) {
            return ResponseEntity.badRequest().body("User does not exist");
        }
        if(!validatePassword(request.getUsername(), request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }
        String token = authService.generateToken(request.getUsername());
        String refreshToken = authService.generateRefreshToken(request.getUsername());
        return ResponseEntity.ok(TokenResponse.builder().token(token).refreshToken(refreshToken).creationDate(LocalDateTime.now()).build());
    }

    private boolean isUserExists(String username) {
        return urepo.findByUserName(username) != null;
    }

    private boolean validatePassword(String username, String password) {
        User user = urepo.findByUserName(username);
        return user != null && endcoder.matches(password, user.getPassword());
    }
}
