package com.brew.auth.dto;

public record WorkspaceRegistrationResponse(
    String workspaceId,
    String slug,
    String apiKey
) {}
