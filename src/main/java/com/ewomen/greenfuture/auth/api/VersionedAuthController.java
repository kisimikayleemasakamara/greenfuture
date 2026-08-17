package com.ewomen.greenfuture.auth.api;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ewomen.greenfuture.auth.application.AuthApplicationService;
import com.ewomen.greenfuture.auth.application.IssuedSession;
import com.ewomen.greenfuture.common.api.ApiResponse;
import com.ewomen.greenfuture.common.api.RequestIdFilter;
import com.ewomen.greenfuture.common.error.ApiException;
import com.ewomen.greenfuture.entity.User;
import com.ewomen.greenfuture.security.RolePermissions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class VersionedAuthController {

    static final String REFRESH_COOKIE = "plastinova_refresh";

    private final AuthApplicationService authApplicationService;
    private final Duration accessTokenTtl;
    private final Duration refreshTokenTtl;
    private final boolean secureCookie;

    public VersionedAuthController(
            AuthApplicationService authApplicationService,
            @Value("${security.jwt.access-token-ttl:15m}") Duration accessTokenTtl,
            @Value("${security.refresh-token.ttl:30d}") Duration refreshTokenTtl,
            @Value("${security.refresh-token.cookie-secure:true}") boolean secureCookie) {
        this.authApplicationService = authApplicationService;
        this.accessTokenTtl = accessTokenTtl;
        this.refreshTokenTtl = refreshTokenTtl;
        this.secureCookie = secureCookie;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest) {
        return sessionResponse(
                authApplicationService.login(request.email(), request.password()),
                servletRequest);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletRequest servletRequest) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new ApiException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "INVALID_REFRESH_SESSION",
                    "The refresh session is invalid or expired.");
        }
        return sessionResponse(
                authApplicationService.rotateRefreshSession(refreshToken),
                servletRequest);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken,
            HttpServletRequest servletRequest) {
        authApplicationService.logout(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredRefreshCookie().toString())
                .body(ApiResponse.of(null, requestId(servletRequest)));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(
            Authentication authentication,
            HttpServletRequest servletRequest) {
        User user = authApplicationService.currentUser(authentication.getName());
        String role = user.getRole().name();
        return ApiResponse.of(
                new CurrentUserResponse(
                        user.getFullName(),
                        user.getEmail(),
                        List.of(role),
                        RolePermissions.getPermissions(role)),
                requestId(servletRequest));
    }

    private ResponseEntity<ApiResponse<AccessTokenResponse>> sessionResponse(
            IssuedSession session,
            HttpServletRequest request) {
        AccessTokenResponse response = new AccessTokenResponse(
                session.accessToken(),
                accessTokenTtl.toSeconds());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(session.refreshToken()).toString())
                .body(ApiResponse.of(response, requestId(request)));
    }

    private ResponseCookie refreshCookie(String value) {
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(refreshTokenTtl)
                .build();
    }

    private ResponseCookie expiredRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE, "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(Duration.ZERO)
                .build();
    }

    private String requestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(RequestIdFilter.ATTRIBUTE_NAME);
        return requestId == null ? "unavailable" : requestId.toString();
    }
}
