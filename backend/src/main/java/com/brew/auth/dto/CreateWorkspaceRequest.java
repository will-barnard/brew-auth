package com.brew.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWorkspaceRequest(
    @NotBlank String name,
    @NotBlank String slug,
    String url
) {}
