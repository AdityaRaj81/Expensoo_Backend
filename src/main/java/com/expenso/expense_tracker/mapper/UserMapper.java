package com.expenso.expense_tracker.mapper;

import com.expenso.expense_tracker.dto.user.UserResponse;
import com.expenso.expense_tracker.model.User;

import org.mapstruct.Mapper;

import java.util.List;

/**
 * User Mapper
 *
 * Converts User entities into DTOs.
 */

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    /**
     * Entity -> Response
     */
    UserResponse toUserResponse(User user);

    /**
     * Entity List -> Response List
     */
    List<UserResponse> toUserResponseList(List<User> users);

}