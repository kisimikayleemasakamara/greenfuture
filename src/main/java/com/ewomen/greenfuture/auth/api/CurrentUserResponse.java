package com.ewomen.greenfuture.auth.api;

import java.util.List;

public record CurrentUserResponse(
        String fullName,
        String email,
        List<String> roles,
        List<String> permissions) {
}
