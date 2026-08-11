package com.example.flowstate.mapper;

import com.example.flowstate.dto.request.UserRequest;
import com.example.flowstate.dto.response.UserResponse;
import com.example.flowstate.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserWebMapper {

    public User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}