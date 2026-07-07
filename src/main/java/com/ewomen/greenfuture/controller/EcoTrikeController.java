package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.entity.EcoTrike;
import com.ewomen.greenfuture.service.EcoTrikeService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecotrikes")
public class EcoTrikeController {

    private final EcoTrikeService ecoTrikeService;

    public EcoTrikeController(
            EcoTrikeService ecoTrikeService) {
        this.ecoTrikeService = ecoTrikeService;
    }

    // ================= CREATE ECOTRIKE =================
    @PreAuthorize("hasAuthority('can_manage_ecotrikes')")
    @PostMapping
    public EcoTrike create(@RequestBody EcoTrike ecoTrike) {
        return ecoTrikeService.save(ecoTrike);
    }

    // ================= VIEW ECOTRIKES =================
    @PreAuthorize("hasAuthority('can_view_ecotrikes')")
    @GetMapping
    public List<EcoTrike> getAll() {
        return ecoTrikeService.getAll();
    }
}