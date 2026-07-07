package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.entity.EcoTrike;
import com.ewomen.greenfuture.repository.EcoTrikeRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class EcoTrikeService {

    private final EcoTrikeRepository ecoTrikeRepository;

    public EcoTrikeService(
            EcoTrikeRepository ecoTrikeRepository) {
        this.ecoTrikeRepository = ecoTrikeRepository;
    }

    public EcoTrike save(EcoTrike ecoTrike) {
        return ecoTrikeRepository.save(ecoTrike);
    }

    public List<EcoTrike> getAll() {
        return ecoTrikeRepository.findAll();
    }
}