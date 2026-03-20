package com.brew.auth.service;

import com.brew.auth.dto.WorkspaceMemberDto;
import com.brew.auth.entity.User;
import com.brew.auth.entity.Workspace;
import com.brew.auth.entity.WorkspaceMembership;
import com.brew.auth.repository.UserRepository;
import com.brew.auth.repository.WorkspaceMembershipRepository;
import com.brew.auth.repository.WorkspaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository,
                            WorkspaceMembershipRepository membershipRepository,
                            UserRepository userRepository) {
        this.workspaceRepository = workspaceRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    public List<Workspace> findAll() {
        return workspaceRepository.findAll();
    }

    public Optional<Workspace> findById(String id) {
        return workspaceRepository.findById(id);
    }

    public Optional<Workspace> findBySlug(String slug) {
        return workspaceRepository.findBySlug(slug);
    }

    public Optional<Workspace> findByApiKey(String apiKey) {
        return workspaceRepository.findByApiKey(apiKey);
    }

    public Workspace create(String name, String slug, String url) {
        if (workspaceRepository.findBySlug(slug).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Workspace slug already exists");
        }

        Workspace ws = new Workspace();
        ws.setName(name);
        ws.setSlug(slug);
        ws.setUrl(url);
        ws.setApiKey(UUID.randomUUID().toString());
        ws.setCreatedAt(LocalDateTime.now());
        return workspaceRepository.save(ws);
    }

    public void delete(String id) {
        membershipRepository.findByWorkspaceId(id).forEach(m -> membershipRepository.delete(m));
        workspaceRepository.deleteById(id);
    }

    public List<WorkspaceMemberDto> getMembers(String workspaceId) {
        return membershipRepository.findByWorkspaceId(workspaceId).stream()
            .map(m -> new WorkspaceMemberDto(
                m.getId(),
                m.getUser().getId(),
                m.getUser().getEmail(),
                m.getUser().getUsername(),
                m.getRole()
            ))
            .toList();
    }

    @Transactional
    public WorkspaceMembership addMember(String workspaceId, String userId, String role) {
        Workspace ws = workspaceRepository.findById(workspaceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found"));
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (membershipRepository.findByUserIdAndWorkspaceId(userId, workspaceId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already a member of this workspace");
        }

        WorkspaceMembership membership = new WorkspaceMembership();
        membership.setWorkspace(ws);
        membership.setUser(user);
        membership.setRole(role);
        membership.setCreatedAt(LocalDateTime.now());
        return membershipRepository.save(membership);
    }

    @Transactional
    public void removeMember(String workspaceId, String userId) {
        membershipRepository.deleteByUserIdAndWorkspaceId(userId, workspaceId);
    }

    /**
     * Get all workspace memberships for a user, used for JWT claims.
     */
    public List<WorkspaceMembership> getMembershipsForUser(String userId) {
        return membershipRepository.findByUserId(userId);
    }
}
