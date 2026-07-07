package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.dto.LoginRequest;
import com.ewomen.greenfuture.dto.LoginResponse;
import com.ewomen.greenfuture.dto.RegisterRequest;
import com.ewomen.greenfuture.dto.UserProfile;

import com.ewomen.greenfuture.entity.User;

import com.ewomen.greenfuture.security.RolePermissions;

import com.ewomen.greenfuture.service.JwtService;
import com.ewomen.greenfuture.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {

        return userService.register(request);
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        User user = userService.findByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword());

        if (!passwordMatches) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(
                new LoginResponse(token));
    }

    // ================= CURRENT USER =================
    @PreAuthorize("hasAuthority('can_view_dashboard')")
    @GetMapping("/me")
    public UserProfile getCurrentUser(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userService.findByEmail(email);

        return new UserProfile(
                user.getEmail(),
                user.getRole().name(),
                RolePermissions.getPermissions(
                        user.getRole().name()));
    }
}
