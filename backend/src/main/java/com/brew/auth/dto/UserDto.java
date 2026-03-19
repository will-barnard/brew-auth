package com.brew.auth.dto;

import com.brew.auth.entity.User;
import java.time.LocalDateTime;

public record UserDto(
    String id,
    String email,
    String username,
    String role,
    LocalDateTime createdAt
) {
    public static UserDto from(User user) {
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            user.getRole(),
            user.getCreatedAt()
        );
    }
}
