package com.brew.auth.config;

import com.brew.auth.entity.User;
import com.brew.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.super-admin-email}")
    private String superAdminEmail;

    @Value("${auth.super-admin-password:}")
    private String superAdminPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            if (superAdminPassword == null || superAdminPassword.isBlank()) {
                log.error("SUPER_ADMIN_PASSWORD must be set for initial bootstrap");
                throw new RuntimeException("SUPER_ADMIN_PASSWORD is required for first-time setup");
            }

            User admin = new User();
            admin.setEmail(superAdminEmail);
            admin.setUsername("Super Admin");
            admin.setPasswordHash(passwordEncoder.encode(superAdminPassword));
            admin.setRole("super_admin");
            admin.setPasswordChangeRequired(true);
            admin.setCreatedAt(LocalDateTime.now());
            userRepository.save(admin);

            log.info("Created default super admin account: {}", superAdminEmail);
            log.info("Password change will be required on first login");
        }
    }
}
