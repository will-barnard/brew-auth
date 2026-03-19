package com.brew.auth.repository;

import com.brew.auth.entity.CryptoKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CryptoKeyRepository extends JpaRepository<CryptoKey, String> {
}
