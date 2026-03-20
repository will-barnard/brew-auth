package com.brew.auth.repository;

import com.brew.auth.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
    Optional<Workspace> findBySlug(String slug);
    Optional<Workspace> findByApiKey(String apiKey);
}
