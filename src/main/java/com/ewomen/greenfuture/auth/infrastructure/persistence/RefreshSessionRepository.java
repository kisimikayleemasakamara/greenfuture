package com.ewomen.greenfuture.auth.infrastructure.persistence;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshSessionRepository extends JpaRepository<RefreshSessionEntity, UUID> {

    Optional<RefreshSessionEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            update RefreshSessionEntity session
               set session.revokedAt = :revokedAt
             where session.familyId = :familyId
               and session.revokedAt is null
            """)
    int revokeActiveFamily(@Param("familyId") UUID familyId, @Param("revokedAt") Instant revokedAt);
}
