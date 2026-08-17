package com.ewomen.greenfuture.auth.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.ewomen.greenfuture.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_session")
public class RefreshSessionEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "replaced_by_session_id")
    private UUID replacedBySessionId;

    protected RefreshSessionEntity() {
    }

    public RefreshSessionEntity(UUID id, User user, UUID familyId, String tokenHash,
            Instant createdAt, Instant expiresAt) {
        this.id = id;
        this.user = user;
        this.familyId = familyId;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public UUID getFamilyId() { return familyId; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getRevokedAt() { return revokedAt; }

    public void revoke(Instant revokedAt, UUID replacedBySessionId) {
        this.revokedAt = revokedAt;
        this.replacedBySessionId = replacedBySessionId;
    }
}
