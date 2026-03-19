package com.brew.auth.repository;

import com.brew.auth.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, String> {
    Optional<Invitation> findByToken(String token);
    Optional<Invitation> findByEmail(String email);
}
