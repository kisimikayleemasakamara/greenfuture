package com.ewomen.greenfuture.service;

import org.springframework.stereotype.Service;
import com.ewomen.greenfuture.entity.Community;

@Service
public class ScoringService {

    public int calculateScore(Community community) {

        int score = 0;

        // Resolved waste reports
        score += community.getResolvedReports() * 10;

        // Cleanup activities
        score += community.getCleanUpActivities() * 20;

        // Penalized for to many unresolved waste reports
        score -= community.getWasteReports() * 2;

        return score;
    }
}
