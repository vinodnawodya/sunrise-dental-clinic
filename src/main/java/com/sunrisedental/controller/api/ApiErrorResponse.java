package com.sunrisedental.controller.api;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(Instant timestamp, int status, String message, Map<String, String> fieldErrors) {

    public ApiErrorResponse(int status, String message) {
        this(Instant.now(), status, message, Map.of());
    }

    public ApiErrorResponse(int status, String message, Map<String, String> fieldErrors) {
        this(Instant.now(), status, message, fieldErrors);
    }
}
