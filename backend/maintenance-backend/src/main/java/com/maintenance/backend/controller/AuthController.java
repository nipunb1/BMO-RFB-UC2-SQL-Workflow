package com.maintenance.backend.controller;

import com.maintenance.backend.model.User;
import com.maintenance.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> userOpt = userService.getUserByEmail(loginRequest.getEmail());
            
            if (userOpt.isPresent() && 
                userService.validatePassword(loginRequest.getEmail(), loginRequest.getPassword())) {
                
                User user = userOpt.get();
                
                if (user.getStatus() != User.UserStatus.ACTIVE) {
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "User account is not active"));
                }
                
                Map<String, Object> response = new HashMap<>();
                response.put("user", createUserResponse(user));
                response.put("token", "mock-jwt-token-" + user.getId());
                response.put("message", "Login successful");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid email or password"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(Map.of(
                "user", createUserResponse(createdUser),
                "message", "User registered successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String userId = token.replace("Bearer mock-jwt-token-", "");
            Optional<User> userOpt = userService.getUserById(Long.parseLong(userId));
            
            if (userOpt.isPresent()) {
                return ResponseEntity.ok(createUserResponse(userOpt.get()));
            } else {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid token"));
        }
    }
    
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("name", user.getName());
        userResponse.put("email", user.getEmail());
        userResponse.put("role", user.getRole());
        userResponse.put("status", user.getStatus());
        return userResponse;
    }
    
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
