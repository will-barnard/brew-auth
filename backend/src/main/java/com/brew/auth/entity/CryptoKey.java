package com.brew.auth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_keys")
public class CryptoKey {

    @Id
    private String id;

    @Column(nullable = false)
    private String kid;

    @Column(name = "public_key_json", nullable = false, columnDefinition = "TEXT")
    private String publicKeyJson;

    @Column(name = "private_key_json", nullable = false, columnDefinition = "TEXT")
    private String privateKeyJson;

    @Column(nullable = false)
    private String algorithm;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getKid() { return kid; }
    public void setKid(String kid) { this.kid = kid; }

    public String getPublicKeyJson() { return publicKeyJson; }
    public void setPublicKeyJson(String publicKeyJson) { this.publicKeyJson = publicKeyJson; }

    public String getPrivateKeyJson() { return privateKeyJson; }
    public void setPrivateKeyJson(String privateKeyJson) { this.privateKeyJson = privateKeyJson; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
