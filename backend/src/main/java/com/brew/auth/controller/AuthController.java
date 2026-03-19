package com.brew.auth.controller;

import com.brew.auth.dto.*;
import com.brew.auth.entity.User;
import com.brew.auth.service.InvitationService;
import com.brew.auth.service.JwtService;
import com.brew.auth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${auth.cookie-domain}")
    private String cookieDomain;

    @Value("${auth.jwt-expiration-hours}")
    private int expirationHours;

    private final UserService userService;
    private final JwtService jwtService;
    private final InvitationService invitationService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtService jwtService,
                          InvitationService invitationService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.invitationService = invitationService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        User user = userService.findByEmail(request.email())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        try {
            String token = jwtService.generateToken(user);
            setCookie(response, token);
            return ResponseEntity.ok(new AuthResponse(token, UserDto.from(user), user.isPasswordChangeRequired()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletResponse response) {
        User user = invitationService.acceptInvitation(request.token(), request.username(), request.password());

        try {
            String token = jwtService.generateToken(user);
            setCookie(response, token);
            return ResponseEntity.ok(new AuthResponse(token, UserDto.from(user), false));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token");
        }
    }

    @PostMapping("/change-password")
    @SuppressWarnings("unchecked")
    public ResponseEntity<AuthResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                        HttpServletResponse httpResponse,
                                                        Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String userId = (String) claims.get("sub");

        User user = userService.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setPasswordChangeRequired(false);
        userService.save(user);

        try {
            String token = jwtService.generateToken(user);
            setCookie(httpResponse, token);
            return ResponseEntity.ok(new AuthResponse(token, UserDto.from(user), false));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate token");
        }
    }

    @GetMapping("/me")
    @SuppressWarnings("unchecked")
    public ResponseEntity<UserDto> me(Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        String userId = (String) claims.get("sub");

        User user = userService.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(UserDto.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("brew_token", "")
            .domain(cookieDomain)
            .path("/")
            .httpOnly(true)
            .secure(!cookieDomain.equals(".localhost"))
            .sameSite("Lax")
            .maxAge(0)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    private void setCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("brew_token", token)
            .domain(cookieDomain)
            .path("/")
            .httpOnly(true)
            .secure(!cookieDomain.equals(".localhost"))
            .sameSite("Lax")
            .maxAge(Duration.ofHours(expirationHours))
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
