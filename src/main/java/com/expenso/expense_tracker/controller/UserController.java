package com.expenso.expense_tracker.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.expenso.expense_tracker.service.UserService;
import com.expenso.expense_tracker.model.User;
import com.expenso.expense_tracker.dto.LoginDTO;


@RestController

@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        String result = userService.loginUser(loginDTO);

        switch (result) {
            case "user_not_found":
                return ResponseEntity.status(404).body("User not found with this email.");
            case "password_wrong":
                return ResponseEntity.status(401).body("Incorrect password.");
            default:
                return ResponseEntity.ok(result); // Could be user's name or welcome message
        }
    }



}
