package com.brew.auth.dto;

public record WorkspaceMemberDto(
    String id,
    String userId,
    String email,
    String username,
    String role
) {}
