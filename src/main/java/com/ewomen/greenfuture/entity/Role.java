package com.ewomen.greenfuture.entity;

import java.util.Set;

public enum Role {

    ADMIN(
            Set.of(
                    "can_view_dashboard",
                    "can_manage_users",
                    "can_manage_reports",
                    "can_manage_trikes",
                    "can_manage_routes",
                    "can_manage_communities",
                    "can_manage_ecotrikes",
                    "can_view_ecotrikes",
                    "can_view_reports",
                    "can_submit_reports",
                    "can_upload_files")),

    COMMUNITY_LEADER(
            Set.of(
                    "can_view_dashboard",
                    "can_manage_communities",
                    "can_view_reports",
                    "can_submit_reports",
                    "can_upload_files")),

    ECOTRIKE_OPERATOR(
            Set.of(
                    "can_view_dashboard",
                    "can_manage_routes",
                    "can_view_ecotrikes",
                    "can_upload_files")),

    CITIZEN(
            Set.of(
                    "can_view_dashboard",
                    "can_submit_reports"));

    private final Set<String> permissions;

    Role(Set<String> permissions) {

        this.permissions = permissions;
    }

    public Set<String> getPermissions() {

        return permissions;
    }
}
