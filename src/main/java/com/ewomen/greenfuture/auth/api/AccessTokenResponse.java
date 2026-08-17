package com.ewomen.greenfuture.auth.api;

public record AccessTokenResponse(String accessToken, long expiresInSeconds) {
}
