package com.george.usermanagementapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request payload for a full user update (PUT /api/v1/users/{id}).
 *
 * <p>All fields except {@code phoneNumber} are required. Every field of the
 * existing user record will be overwritten with the values in this request.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload for fully replacing an existing user")
public class UpdateUserRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Schema(description = "User's first name", example = "Jane", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Schema(description = "User's last name", example = "Smith", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Schema(description = "Unique email address", example = "jane.smith@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    @Schema(description = "Optional contact phone number", example = "+1-555-987-6543")
    private String phoneNumber;
}
