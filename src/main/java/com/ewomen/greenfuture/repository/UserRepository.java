package com.ewomen.greenfuture.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ewomen.greenfuture.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    long count();

    Optional<User> findByEmail(String email);
}
