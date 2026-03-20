package com.brew.auth.controller;

import com.brew.auth.dto.*;
import com.brew.auth.entity.Workspace;
import com.brew.auth.entity.WorkspaceMembership;
import com.brew.auth.service.WorkspaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<WorkspaceDto>> list(Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String role = (String) claims.get("role");

        if (!"super_admin".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only super admins can manage workspaces");
        }

        return ResponseEntity.ok(
            workspaceService.findAll().stream().map(WorkspaceDto::from).toList()
        );
    }

    @PostMapping
    @SuppressWarnings("unchecked")
    public ResponseEntity<WorkspaceRegistrationResponse> create(
            @Valid @RequestBody CreateWorkspaceRequest request,
            Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String role = (String) claims.get("role");

        if (!"super_admin".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only super admins can create workspaces");
        }

        Workspace ws = workspaceService.create(request.name(), request.slug(), request.url());
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new WorkspaceRegistrationResponse(ws.getId(), ws.getSlug(), ws.getApiKey())
        );
    }

    /**
     * Register a workspace from an external Beachhead instance.
     * Authenticated via the super admin's token.
     */
    @PostMapping("/register")
    @SuppressWarnings("unchecked")
    public ResponseEntity<WorkspaceRegistrationResponse> register(
            @Valid @RequestBody CreateWorkspaceRequest request,
            Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String role = (String) claims.get("role");
        String userId = (String) claims.get("sub");

        if (!"super_admin".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only super admins can register workspaces");
        }

        Workspace ws = workspaceService.create(request.name(), request.slug(), request.url());

        // Automatically add the registering user as admin of the workspace
        workspaceService.addMember(ws.getId(), userId, "admin");

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new WorkspaceRegistrationResponse(ws.getId(), ws.getSlug(), ws.getApiKey())
        );
    }

    /**
     * Verify a workspace API key. Used by Beachhead instances to validate
     * their connection to brew-auth without requiring a user token.
     */
    @PostMapping("/verify")
    public ResponseEntity<WorkspaceDto> verifyApiKey(@RequestBody Map<String, String> body) {
        String apiKey = body.get("api_key");
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "api_key is required");
        }

        Workspace ws = workspaceService.findByApiKey(apiKey)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key"));

        return ResponseEntity.ok(WorkspaceDto.from(ws));
    }

    @DeleteMapping("/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Void> delete(@PathVariable String id, Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String role = (String) claims.get("role");

        if (!"super_admin".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        workspaceService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<WorkspaceMemberDto>> getMembers(
            @PathVariable String id, Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String role = (String) claims.get("role");

        if (!"super_admin".equals(role) && !"admin".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(workspaceService.getMembers(id));
    }

    @PostMapping("/{id}/members")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, String>> addMember(
            @PathVariable String id,
            @Valid @RequestBody WorkspaceMemberRequest request,
            Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String role = (String) claims.get("role");

        if (!"super_admin".equals(role) && !"admin".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        workspaceService.addMember(id, request.userId(), request.role());
        return ResponseEntity.ok(Map.of("message", "Member added"));
    }

    @DeleteMapping("/{id}/members/{userId}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Void> removeMember(
            @PathVariable String id,
            @PathVariable String userId,
            Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String role = (String) claims.get("role");

        if (!"super_admin".equals(role) && !"admin".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        workspaceService.removeMember(id, userId);
        return ResponseEntity.ok().build();
    }
}
