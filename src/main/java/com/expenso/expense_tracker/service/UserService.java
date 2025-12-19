package com.expenso.expense_tracker.service;

import com.expenso.expense_tracker.dto.LoginDTO;
import com.expenso.expense_tracker.model.User;
import com.expenso.expense_tracker.repository.UserRepository;
import com.expenso.expense_tracker.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;


    // ✅ Create User
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // ✅ Login Logic
    public String loginUser(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(loginDTO.getEmail());

        if (userOpt.isEmpty()) {
            return "user_not_found";
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(loginDTO.getPassword())) {
            return "password_wrong";
        }

        // ✅ Return JWT instead of message
        return jwtService.generateToken(user.getId());

    }
}
