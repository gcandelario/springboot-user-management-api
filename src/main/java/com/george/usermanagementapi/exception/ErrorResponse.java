package com.george.usermanagementapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardised error envelope returned by the API for all error conditions.
 *
 * <p>Fields that are {@code null} (e.g. {@code fieldErrors} for a 404) are
 * excluded from the JSON response via {@link JsonInclude}.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response envelope")
public class ErrorResponse {

    @Schema(description = "Timestamp of when the error occurred", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "HTTP reason phrase", example = "Not Found")
    private String error;

    @Schema(description = "Human-readable error description", example = "User not found with id: 42")
    private String message;

    @Schema(description = "Request URI that produced the error", example = "/api/v1/users/42")
    private String path;

    /**
     * Per-field validation errors. Present only for 400 Bad Request responses
     * caused by {@code @Valid} constraint violations.
     *
     * <p>Key = field name, Value = constraint violation message.
     */
    @Schema(description = "Field-level validation errors (only present on 400 responses)",
            example = "{\"email\": \"Email must be a valid email address\"}")
    private Map<String, String> fieldErrors;
}
