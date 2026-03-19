package com.brew.auth.service;

import com.brew.auth.entity.Invitation;
import com.brew.auth.entity.User;
import com.brew.auth.repository.InvitationRepository;
import com.brew.auth.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InvitationService(InvitationRepository invitationRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Invitation createInvitation(String email, String role, String invitedBy) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with this email already exists");
        }

        invitationRepository.findByEmail(email).ifPresent(invitationRepository::delete);

        Invitation invitation = new Invitation();
        invitation.setEmail(email);
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setRole(role);
        invitation.setInvitedBy(invitedBy);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setUsed(false);

        return invitationRepository.save(invitation);
    }

    public User acceptInvitation(String token, String username, String password) {
        Invitation invitation = invitationRepository.findByToken(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid invitation token"));

        if (invitation.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation already used");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invitation expired");
        }

        if (userRepository.findByEmail(invitation.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }

        User user = new User();
        user.setEmail(invitation.getEmail());
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(invitation.getRole());
        user.setPasswordChangeRequired(false);
        user.setCreatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        invitation.setUsed(true);
        invitationRepository.save(invitation);

        return user;
    }
}
