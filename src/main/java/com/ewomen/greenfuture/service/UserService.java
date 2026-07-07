package com.ewomen.greenfuture.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ewomen.greenfuture.dto.RegisterRequest;
import com.ewomen.greenfuture.entity.Community;
import com.ewomen.greenfuture.entity.User;
import com.ewomen.greenfuture.repository.CommunityRepository;
import com.ewomen.greenfuture.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            CommunityRepository communityRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {

        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(() -> new RuntimeException("Community not found"));
        User user = new User();

        user.setFullName(request.getFullname());
        user.setEmail(request.getEmail());

        // Encrypt the password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(request.getRole());

        user.setCommunity(community);

        return userRepository.save(user);

    }

    public User findByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
