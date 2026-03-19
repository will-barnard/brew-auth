package com.brew.auth.controller;

import com.brew.auth.service.AppSettingService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final AppSettingService appSettingService;

    public SettingsController(AppSettingService appSettingService) {
        this.appSettingService = appSettingService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> getAll() {
        return ResponseEntity.ok(appSettingService.getAll());
    }

    @PutMapping("/{key}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Void> update(@PathVariable String key,
                                        @RequestBody Map<String, String> body,
                                        Authentication auth) {
        Map<String, Object> claims = (Map<String, Object>) auth.getPrincipal();
        if (!"super_admin".equals(claims.get("role"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "super_admin required");
        }

        String value = body.get("value");
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "value is required");
        }
        if (value.length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "value too long");
        }

        appSettingService.set(key, value);
        return ResponseEntity.ok().build();
    }
}
