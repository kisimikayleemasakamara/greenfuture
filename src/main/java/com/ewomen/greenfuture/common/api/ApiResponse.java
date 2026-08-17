package com.ewomen.greenfuture.common.api;

public record ApiResponse<T>(T data, ApiResponseMeta meta) {

    public static <T> ApiResponse<T> of(T data, String requestId) {
        return new ApiResponse<>(data, new ApiResponseMeta(requestId));
    }
}
