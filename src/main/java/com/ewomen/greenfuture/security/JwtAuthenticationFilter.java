package com.ewomen.greenfuture.security;

import com.ewomen.greenfuture.entity.User;
import com.ewomen.greenfuture.service.JwtService;
import com.ewomen.greenfuture.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String email = null;
        String token = null;

        // ================= EXTRACT TOKEN =================
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            if (jwtService.isTokenValid(token)) {
                email = jwtService.extractEmail(token);
            }
        }

        // ================= AUTHENTICATE =================
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            User user = userService.findByEmail(email);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            // 1. ROLE (Spring standard)
            String role = user.getRole().name();

            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role));

            // 2. PERMISSIONS (FROM RolePermissions CLASS — FIXED)
            List<String> permissions = RolePermissions.getPermissions(role);

            authorities.addAll(
                    permissions.stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList());

            // ================= SET AUTH CONTEXT =================
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    authorities);

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
