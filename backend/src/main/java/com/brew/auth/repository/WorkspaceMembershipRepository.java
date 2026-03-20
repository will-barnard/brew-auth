package com.brew.auth.repository;

import com.brew.auth.entity.WorkspaceMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMembershipRepository extends JpaRepository<WorkspaceMembership, String> {
    List<WorkspaceMembership> findByUserId(String userId);
    List<WorkspaceMembership> findByWorkspaceId(String workspaceId);
    Optional<WorkspaceMembership> findByUserIdAndWorkspaceId(String userId, String workspaceId);
    void deleteByUserIdAndWorkspaceId(String userId, String workspaceId);
}
