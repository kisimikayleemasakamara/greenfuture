package com.ewomen.greenfuture.security;

import java.util.List;

import com.ewomen.greenfuture.entity.Role;

public class RolePermissions {

    private RolePermissions() {
    }

    public static List<String> getPermissions(String role) {
        try {
            return Role.valueOf(role).getPermissions().stream().sorted().toList();
        } catch (IllegalArgumentException exception) {
            return List.of();
        }
    }
}
