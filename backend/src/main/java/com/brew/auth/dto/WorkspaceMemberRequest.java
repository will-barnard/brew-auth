package com.brew.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record WorkspaceMemberRequest(
    @NotBlank String userId,
    @NotBlank String role
) {}
