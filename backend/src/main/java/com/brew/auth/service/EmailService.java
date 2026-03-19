package com.brew.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${resend.api-key:}")
    private String apiKey;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${auth.url}")
    private String authUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendInvitationEmail(String toEmail, String token) {
        String registerUrl = authUrl + "/register?token=" + token;

        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Resend API key not configured, skipping email to {}", toEmail);
            log.info("Registration link: {}", registerUrl);
            return;
        }

        Map<String, Object> body = Map.of(
            "from", fromEmail,
            "to", List.of(toEmail),
            "subject", "You've been invited to create an account",
            "html", """
                <h2>You've been invited!</h2>
                <p>Click the link below to create your account:</p>
                <p><a href="%s">Create Account</a></p>
                <p>This invitation expires in 7 days.</p>
                """.formatted(registerUrl)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
            log.info("Invitation email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send invitation email to {}: {}", toEmail, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send invitation email");
        }
    }
}
