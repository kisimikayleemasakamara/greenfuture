package com.ewomen.greenfuture.entity;

import java.util.Set;

public enum Role {

    ADMIN(
            Set.of(
                    "can_view_dashboard",
                    "can_manage_users",
                    "can_manage_reports",
                    "can_manage_trikes")),

    COMMUNITY_LEADER(
            Set.of(
                    "can_view_dashboard")),

    ECOTRIKE_OPERATOR(
            Set.of(
                    "can_view_dashboard")),

    CITIZEN(
            Set.of());

    private final Set<String> permissions;

    Role(Set<String> permissions) {

        this.permissions = permissions;
    }

    public Set<String> getPermissions() {

        return permissions;
    }
}