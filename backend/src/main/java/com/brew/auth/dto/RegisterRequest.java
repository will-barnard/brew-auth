package com.brew.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank String token,
    @NotBlank @Size(min = 2) String username,
    @NotBlank @Size(min = 8) String password
) {}
