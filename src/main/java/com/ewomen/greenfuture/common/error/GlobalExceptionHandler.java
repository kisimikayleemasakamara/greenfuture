package com.ewomen.greenfuture.common.error;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ewomen.greenfuture.common.api.RequestIdFilter;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorEnvelope> handleApiException(
            ApiException exception,
            HttpServletRequest request) {

        return errorResponse(
                exception.status(),
                exception.code(),
                exception.getMessage(),
                List.of(),
                requestId(request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorEnvelope> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        List<ApiFieldError> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(this::toApiFieldError)
                .toList();

        return errorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                "The request could not be accepted.",
                fieldErrors,
                requestId(request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorEnvelope> handleUnexpected(
            Exception exception,
            HttpServletRequest request) {

        String requestId = requestId(request);
        LOGGER.error("Unexpected request failure requestId={}", requestId, exception);

        return errorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "An unexpected error occurred.",
                List.of(),
                requestId);
    }

    private ApiFieldError toApiFieldError(FieldError fieldError) {
        String code = fieldError.getCode() == null ? "INVALID" : fieldError.getCode().toUpperCase();
        String message = fieldError.getDefaultMessage() == null ? "Invalid value." : fieldError.getDefaultMessage();
        return new ApiFieldError(fieldError.getField(), code, message);
    }

    private ResponseEntity<ApiErrorEnvelope> errorResponse(
            HttpStatus status,
            String code,
            String message,
            List<ApiFieldError> fieldErrors,
            String requestId) {

        ApiError error = new ApiError(code, message, fieldErrors, requestId);
        return ResponseEntity.status(status).body(new ApiErrorEnvelope(error));
    }

    private String requestId(HttpServletRequest request) {
        Object requestId = request.getAttribute(RequestIdFilter.ATTRIBUTE_NAME);
        return requestId == null ? "unavailable" : requestId.toString();
    }
}
