package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.entity.Community;
import com.ewomen.greenfuture.repository.CommunityRepository;
import org.springframework.stereotype.Service;

@Service
public class CommunityScoreService {

    private final CommunityRepository communityRepository;

    public CommunityScoreService(
            CommunityRepository communityRepository) {
        this.communityRepository = communityRepository;
    }

    public void rewardResolvedWaste(
            Community community) {

        if (community.getScore() == null) {
            community.setScore(0);
        }

        community.setScore(
                community.getScore() + 10);

        communityRepository.save(community);
    }
}