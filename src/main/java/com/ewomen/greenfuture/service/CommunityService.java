package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.entity.Community;
import com.ewomen.greenfuture.repository.CommunityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityService {
    private CommunityRepository communityRepository;
    private ScoringService scoringService;

    public CommunityService(CommunityRepository communityRepository,
            ScoringService scoringService) {
        this.communityRepository = communityRepository;
        this.scoringService = scoringService;
    }

    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }

    @SuppressWarnings("null")
    public Community saveCommunity(Community community) {
        int calculatedScore = scoringService.calculateScore(community);

        community.setScore(calculatedScore);

        return communityRepository.save(community);
    }

    public List<Community> getLeaderBoard() {
        return this.communityRepository.findAllByOrderByScoreDesc();
    }

}
