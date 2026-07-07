package com.ewomen.greenfuture.security;

import java.util.List;
import java.util.Map;

public class RolePermissions {

    public static final Map<String, List<String>> ROLE_PERMISSIONS = Map.of(

            "ADMIN", List.of(
                    "can_view_dashboard",
                    "can_manage_users",
                    "can_manage_routes"),

            "OPERATOR", List.of(
                    "can_view_dashboard",
                    "can_manage_routes"),

            "USER", List.of(
                    "can_view_dashboard"));

    public static List<String> getPermissions(String role) {
        return ROLE_PERMISSIONS.getOrDefault(role, List.of());
    }
}