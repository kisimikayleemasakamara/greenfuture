package com.ewomen.greenfuture.repository;

import com.ewomen.greenfuture.entity.Community;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    long count();

    List<Community> findAllByOrderByScoreDesc();

    @Query("""
                SELECT c
                FROM Community c
                ORDER BY c.score DESC
            """)
    List<Community> findTopCommunities();
}
