package com.expenso.expense_tracker.controller;

import com.expenso.expense_tracker.dto.LoginRequest;
import com.expenso.expense_tracker.model.User;
import com.expenso.expense_tracker.repository.UserRepository;
import com.expenso.expense_tracker.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService; // ✅ moved above usage

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {

        // Always save emails in lowercase
        String normalizedEmail = user.getEmail().toLowerCase();

        if (userRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            return "Email already registered!";
        }
        user.setEmail(normalizedEmail);
        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email"));
        }

        User user = userOpt.get();

        // Check if user is active
        if (!user.getActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "User account is blocked"));
        }

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid password"));
        }

        // ✅ Generate JWT token with role claim
        String token = jwtService.generateToken(user.getId(), user.getRole());

        // ✅ Return token and user with role
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole()));

        return ResponseEntity.ok(response);
    }
}
