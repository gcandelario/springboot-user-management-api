package com.george.usermanagementapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for a partial user update (PATCH /api/v1/users/{id}).
 *
 * <p>All fields are optional. Only non-{@code null} fields sent in the request
 * body will be applied to the existing user record. Fields omitted from the
 * JSON body (or explicitly set to {@code null}) are left unchanged.
 *
 * <p>Validation constraints still apply to any value that <em>is</em> provided.
 * For example, if {@code email} is present it must be a valid email format.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for partially updating an existing user — only provided fields are applied")
public class PatchUserRequest {

    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Schema(description = "New first name (optional)", example = "Jane")
    private String firstName;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Schema(description = "New last name (optional)", example = "Smith")
    private String lastName;

    @Email(message = "Email must be a valid email address")
    @Schema(description = "New email address (optional, must be unique)", example = "jane.smith@example.com")
    private String email;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Schema(description = "New phone number (optional)", example = "+1-555-987-6543")
    private String phoneNumber;
}
