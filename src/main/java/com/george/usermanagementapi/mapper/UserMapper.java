package com.george.usermanagementapi.mapper;

import com.george.usermanagementapi.dto.request.CreateUserRequest;
import com.george.usermanagementapi.dto.request.PatchUserRequest;
import com.george.usermanagementapi.dto.request.UpdateUserRequest;
import com.george.usermanagementapi.dto.response.UserResponse;
import com.george.usermanagementapi.entity.User;
import org.springframework.stereotype.Component;

/**
 * Responsible for converting between {@link User} entities and their DTO
 * representations.
 *
 * <p>Keeping mapping logic in a dedicated class prevents the service layer
 * from knowing how entities are constructed, and makes it trivial to swap in
 * a library like MapStruct later without touching business logic.
 */
@Component
public class UserMapper {

    /**
     * Maps a {@link CreateUserRequest} DTO to a new, unpersisted {@link User} entity.
     *
     * <p>Timestamps are intentionally not set here — they are handled by the
     * JPA lifecycle callbacks ({@code @PrePersist}).
     *
     * @param request the incoming create request
     * @return a transient {@link User} ready to be saved
     */
    public User toEntity(CreateUserRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .build();
    }

    /**
     * Maps a persisted {@link User} entity to a {@link UserResponse} DTO.
     *
     * @param user the entity retrieved from the database
     * @return an immutable response DTO safe to return to API callers
     */
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Applies all fields from an {@link UpdateUserRequest} to an existing
     * {@link User} entity (full replacement — PUT semantics).
     *
     * <p>Every field of the entity is overwritten, including {@code phoneNumber}
     * which may be set to {@code null} if the caller omits it.
     *
     * @param user    the managed entity to update (will be modified in-place)
     * @param request the PUT request containing the new field values
     */
    public void updateEntity(User user, UpdateUserRequest request) {
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
    }

    /**
     * Selectively applies non-{@code null} fields from a {@link PatchUserRequest}
     * to an existing {@link User} entity (partial update — PATCH semantics).
     *
     * <p>Any field that is {@code null} in the request is left unchanged on
     * the entity, so callers only need to send the fields they want to modify.
     *
     * @param user    the managed entity to patch (will be modified in-place)
     * @param request the PATCH request — only non-null fields are applied
     */
    public void patchEntity(User user, PatchUserRequest request) {
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
    }
}
