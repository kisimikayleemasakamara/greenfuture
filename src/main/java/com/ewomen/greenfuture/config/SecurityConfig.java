package com.ewomen.greenfuture.config;

import com.ewomen.greenfuture.security.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .cors(cors -> {
                                })
                                .csrf(csrf -> csrf.disable())

                                .httpBasic(httpBasic -> httpBasic.disable())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers("/uploads/**").permitAll()
                                                .requestMatchers("/api/files/**").authenticated()

                                                // Public routes
                                                .requestMatchers("/api/auth/login").permitAll()
                                                .requestMatchers(
                                                                "/api/v1/auth/login",
                                                                "/api/v1/auth/refresh",
                                                                "/api/v1/auth/logout")
                                                .permitAll()

                                                // ADMIN ONLY
                                                .requestMatchers("/api/admin/**")
                                                .hasRole("ADMIN")

                                                // COMMUNITY LEADERS + ADMIN
                                                .requestMatchers("/api/communities/**")
                                                .hasAnyRole("ADMIN", "COMMUNITY_LEADER")

                                                // CITIZENS + ADMIN
                                                .requestMatchers("/api/reports/**")
                                                .hasAnyRole("ADMIN", "CITIZEN")

                                                // ECOTRIKE OPERATORS
                                                .requestMatchers("/api/ecotrikes/**")
                                                .hasAnyRole("ADMIN", "ECOTRIKE_OPERATOR")

                                                // ASSIGNMENTS SECURED
                                                .requestMatchers("/api/assignments/**")
                                                .hasAnyRole("ADMIN", "ECOTRIKE_OPERATOR")

                                                // SECURE ANALYTICS APIs
                                                .requestMatchers("/api/analytics/**")
                                                .authenticated()

                                                // TO ALLOW PUBLIC ACCESS
                                                .requestMatchers("/api/public/**").permitAll()

                                                // Everything else
                                                .anyRequest().authenticated())

                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
