package com.george.usermanagementapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Read-only DTO returned by every user endpoint.
 *
 * <p>The entity is never exposed directly through the API; callers always
 * receive this response object so internal implementation details stay hidden.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User data returned by the API")
public class UserResponse {

    @Schema(description = "Auto-generated unique identifier", example = "1")
    private Long id;

    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Schema(description = "User's unique email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "User's contact phone number", example = "+1-555-123-4567", nullable = true)
    private String phoneNumber;

    @Schema(description = "ISO-8601 timestamp of when the user was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "ISO-8601 timestamp of the most recent update", example = "2024-06-01T08:00:00")
    private LocalDateTime updatedAt;
}
