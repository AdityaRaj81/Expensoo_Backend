package com.expenso.expense_tracker.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.expenso.expense_tracker.dto.user.UserResponse;
import com.expenso.expense_tracker.exception.ResourceNotFoundException;
import com.expenso.expense_tracker.mapper.UserMapper;
import com.expenso.expense_tracker.model.User;
import com.expenso.expense_tracker.repository.UserRepository;
import com.expenso.expense_tracker.security.JwtService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UUID userId) {
        return userMapper.toUserResponse(getUserEntity(userId));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        return userMapper.toUserResponse(getUserEntity(userId));
    }

    public UserResponse updateProfile(UUID userId, String name) {
        User user = getUserEntity(userId);
        user.setName(name.strip());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deactivateAccount(UUID userId) {
        User user = getUserEntity(userId);
        user.setActive(false);
        userRepository.save(user);
    }

    public void reactivateAccount(UUID userId) {
        User user = getUserEntity(userId);
        user.setActive(true);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String token) {
        return getCurrentUser(jwtService.extractUserId(token));
    }

    private User getUserEntity(UUID userId) {
        return userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}