package com.brew.auth.dto;

public record AuthResponse(
    String token,
    UserDto user,
    boolean passwordChangeRequired
) {}
