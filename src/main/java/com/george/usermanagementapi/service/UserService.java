package com.george.usermanagementapi.service;

import com.george.usermanagementapi.dto.request.CreateUserRequest;
import com.george.usermanagementapi.dto.request.PatchUserRequest;
import com.george.usermanagementapi.dto.request.UpdateUserRequest;
import com.george.usermanagementapi.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contract for user management business operations.
 *
 * <p>Programming against this interface (rather than the concrete
 * implementation) keeps callers decoupled and makes unit-testing trivial —
 * any test can substitute a mock or stub without touching production code.
 */
public interface UserService {

    /**
     * Creates a new user from the supplied request payload.
     *
     * @param request validated create request
     * @return the persisted user as a response DTO
     * @throws com.george.usermanagementapi.exception.EmailAlreadyExistsException
     *         if the email is already registered
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Returns a paginated, optionally sorted slice of all users.
     *
     * @param pageable pagination and sorting parameters
     * @return a page of user response DTOs
     */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /**
     * Retrieves a single user by their primary key.
     *
     * @param id the user's ID
     * @return the matching user as a response DTO
     * @throws com.george.usermanagementapi.exception.ResourceNotFoundException
     *         if no user exists with the given ID
     */
    UserResponse getUserById(Long id);

    /**
     * Fully replaces all fields of an existing user (PUT semantics).
     *
     * @param id      the ID of the user to update
     * @param request validated update request containing the new field values
     * @return the updated user as a response DTO
     * @throws com.george.usermanagementapi.exception.ResourceNotFoundException
     *         if no user exists with the given ID
     * @throws com.george.usermanagementapi.exception.EmailAlreadyExistsException
     *         if the new email is already owned by a different user
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Partially updates an existing user — only non-{@code null} fields in
     * the request are applied (PATCH semantics).
     *
     * @param id      the ID of the user to patch
     * @param request patch request; only fields that should change need to be set
     * @return the updated user as a response DTO
     * @throws com.george.usermanagementapi.exception.ResourceNotFoundException
     *         if no user exists with the given ID
     * @throws com.george.usermanagementapi.exception.EmailAlreadyExistsException
     *         if the new email is already owned by a different user
     */
    UserResponse patchUser(Long id, PatchUserRequest request);

    /**
     * Permanently removes a user record.
     *
     * @param id the ID of the user to delete
     * @throws com.george.usermanagementapi.exception.ResourceNotFoundException
     *         if no user exists with the given ID
     */
    void deleteUser(Long id);
}
