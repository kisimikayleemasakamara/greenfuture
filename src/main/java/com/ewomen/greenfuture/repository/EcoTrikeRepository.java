package com.ewomen.greenfuture.repository;

import com.ewomen.greenfuture.entity.EcoTrike;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EcoTrikeRepository
                extends JpaRepository<EcoTrike, Long> {
        long count();
}