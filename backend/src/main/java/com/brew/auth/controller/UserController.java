package com.brew.auth.controller;

import com.brew.auth.dto.InviteRequest;
import com.brew.auth.dto.UserDto;
import com.brew.auth.entity.Invitation;
import com.brew.auth.service.EmailService;
import com.brew.auth.service.InvitationService;
import com.brew.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final InvitationService invitationService;
    private final EmailService emailService;

    public UserController(UserService userService, InvitationService invitationService,
                          EmailService emailService) {
        this.userService = userService;
        this.invitationService = invitationService;
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        return ResponseEntity.ok(
            userService.findAll().stream().map(UserDto::from).toList()
        );
    }

    @PostMapping("/invite")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, String>> invite(@Valid @RequestBody InviteRequest request,
                                                       Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String userId = (String) claims.get("sub");
        String role = (String) claims.get("role");

        if (!"super_admin".equals(role) && !"admin".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions");
        }

        if ("admin".equals(request.role()) && !"super_admin".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only super admins can invite admins");
        }

        if ("super_admin".equals(request.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot invite super admins");
        }

        Invitation invitation = invitationService.createInvitation(request.email(), request.role(), userId);
        emailService.sendInvitationEmail(request.email(), invitation.getToken());

        return ResponseEntity.ok(Map.of("message", "Invitation sent"));
    }

    @DeleteMapping("/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Void> deleteUser(@PathVariable String id, Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String callerRole = (String) claims.get("role");
        String callerId = (String) claims.get("sub");

        if (!"super_admin".equals(callerRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only super admins can delete users");
        }

        if (id.equals(callerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete yourself");
        }

        userService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
