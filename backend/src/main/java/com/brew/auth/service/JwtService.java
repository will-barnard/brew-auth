package com.brew.auth.service;

import com.brew.auth.entity.CryptoKey;
import com.brew.auth.entity.User;
import com.brew.auth.entity.WorkspaceMembership;
import com.brew.auth.repository.CryptoKeyRepository;
import com.brew.auth.repository.WorkspaceMembershipRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final CryptoKeyRepository cryptoKeyRepository;
    private final WorkspaceMembershipRepository membershipRepository;

    private RSAKey rsaKey;
    private JWSSigner signer;
    private JWSVerifier verifier;

    @Value("${auth.issuer}")
    private String issuer;

    @Value("${auth.jwt-expiration-hours}")
    private int expirationHours;

    public JwtService(CryptoKeyRepository cryptoKeyRepository,
                      WorkspaceMembershipRepository membershipRepository) {
        this.cryptoKeyRepository = cryptoKeyRepository;
        this.membershipRepository = membershipRepository;
    }

    @PostConstruct
    public void init() throws Exception {
        CryptoKey key = cryptoKeyRepository.findById("primary").orElse(null);

        if (key == null) {
            log.info("No RSA key found, generating new key pair...");
            RSAKey newKey = new RSAKeyGenerator(2048)
                .keyID(UUID.randomUUID().toString())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .generate();

            key = new CryptoKey();
            key.setId("primary");
            key.setKid(newKey.getKeyID());
            key.setPublicKeyJson(newKey.toPublicJWK().toJSONString());
            key.setPrivateKeyJson(newKey.toJSONString());
            key.setAlgorithm("RS256");
            key.setCreatedAt(LocalDateTime.now());
            cryptoKeyRepository.save(key);

            this.rsaKey = newKey;
            log.info("RSA key pair generated and stored (kid: {})", newKey.getKeyID());
        } else {
            this.rsaKey = RSAKey.parse(key.getPrivateKeyJson());
            log.info("RSA key pair loaded from database (kid: {})", rsaKey.getKeyID());
        }

        this.signer = new RSASSASigner(rsaKey);
        this.verifier = new RSASSAVerifier(rsaKey.toPublicJWK());
    }

    public String generateToken(User user) throws JOSEException {
        // Build workspace membership list for JWT claims
        List<WorkspaceMembership> memberships = membershipRepository.findByUserId(user.getId());
        List<Map<String, String>> workspaceClaims = memberships.stream()
            .map(m -> Map.of(
                "id", m.getWorkspace().getId(),
                "slug", m.getWorkspace().getSlug(),
                "role", m.getRole()
            ))
            .toList();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
            .subject(user.getId())
            .claim("email", user.getEmail())
            .claim("username", user.getUsername())
            .claim("role", user.getRole())
            .claim("password_change_required", user.isPasswordChangeRequired())
            .claim("workspaces", workspaceClaims)
            .issuer(issuer)
            .issueTime(new Date())
            .expirationTime(Date.from(Instant.now().plus(expirationHours, ChronoUnit.HOURS)))
            .build();

        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.getKeyID())
                .build(),
            claims
        );

        signedJWT.sign(signer);
        return signedJWT.serialize();
    }

    public Map<String, Object> validateToken(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);

        if (!signedJWT.verify(verifier)) {
            throw new RuntimeException("Invalid JWT signature");
        }

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        if (claims.getExpirationTime() != null && claims.getExpirationTime().before(new Date())) {
            throw new RuntimeException("JWT expired");
        }

        if (issuer != null && !issuer.equals(claims.getIssuer())) {
            throw new RuntimeException("Invalid issuer");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("sub", claims.getSubject());
        result.put("email", claims.getStringClaim("email"));
        result.put("username", claims.getStringClaim("username"));
        result.put("role", claims.getStringClaim("role"));
        result.put("password_change_required", claims.getBooleanClaim("password_change_required"));
        Object workspaces = claims.getClaim("workspaces");
        result.put("workspaces", workspaces != null ? workspaces : List.of());
        return result;
    }

    public String getJwksJson() {
        JWKSet jwkSet = new JWKSet(rsaKey.toPublicJWK());
        return jwkSet.toString();
    }
}
