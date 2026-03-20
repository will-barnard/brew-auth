package com.brew.auth.dto;

import com.brew.auth.entity.Workspace;
import java.time.LocalDateTime;

public record WorkspaceDto(
    String id,
    String slug,
    String name,
    String url,
    LocalDateTime createdAt
) {
    public static WorkspaceDto from(Workspace ws) {
        return new WorkspaceDto(ws.getId(), ws.getSlug(), ws.getName(), ws.getUrl(), ws.getCreatedAt());
    }
}
