package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.service.CommunityService;
import com.ewomen.greenfuture.entity.Community;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    // ================= VIEW ALL COMMUNITIES =================
    @PreAuthorize("hasAuthority('can_view_dashboard')")
    @GetMapping
    public List<Community> getAllCommunities() {
        return communityService.getAllCommunities();
    }

    // ================= CREATE COMMUNITY =================
    @PreAuthorize("hasAuthority('can_manage_communities')")
    @PostMapping
    public Community createCommunity(@RequestBody Community community) {
        return communityService.saveCommunity(community);
    }

    // ================= LEADERBOARD =================
    @PreAuthorize("hasAuthority('can_view_dashboard')")
    @GetMapping("/leaderboard")
    public List<Community> getLeaderBoard() {
        return this.communityService.getLeaderBoard();
    }

    // ================= TEST / SECURE ENDPOINT =================
    @PreAuthorize("hasAuthority('can_view_dashboard')")
    @GetMapping("/secure")
    public String secureEndpoint() {
        return "This is a protected community endpoint";
    }
}
