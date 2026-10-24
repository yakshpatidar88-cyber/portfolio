package com.ledgerlock.dto;

import java.time.Instant;
import java.util.Map;

/**
 * =====================================================================================
 * DTO: ApiProblemDetail
 * RFC-7807 compliant error payload structure for API errors and constraint violations.
 * =====================================================================================
 */
public record ApiProblemDetail(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        Instant timestamp,
        Map<String, Object> properties
) {
    public static ApiProblemDetail of(String type, String title, int status, String detail, String instance) {
        return new ApiProblemDetail(type, title, status, detail, instance, Instant.now(), Map.of());
    }

    public static ApiProblemDetail of(String type, String title, int status, String detail, String instance, Map<String, Object> properties) {
        return new ApiProblemDetail(type, title, status, detail, instance, Instant.now(), properties);
    }
}