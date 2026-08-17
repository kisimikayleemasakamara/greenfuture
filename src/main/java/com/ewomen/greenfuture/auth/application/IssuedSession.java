package com.ewomen.greenfuture.auth.application;

import com.ewomen.greenfuture.entity.User;

public record IssuedSession(User user, String accessToken, String refreshToken) {
}
