package com.ewomen.greenfuture.dto;

import java.util.List;

public class UserProfile {

    private String email;
    private String role;
    private List<String> permissions;

    public UserProfile(String email, String role, List<String> permissions) {
        this.email = email;
        this.role = role;
        this.permissions = permissions;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}