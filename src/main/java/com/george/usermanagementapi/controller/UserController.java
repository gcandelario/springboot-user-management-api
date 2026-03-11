package com.george.usermanagementapi.controller;

import com.george.usermanagementapi.dto.request.CreateUserRequest;
import com.george.usermanagementapi.dto.request.PatchUserRequest;
import com.george.usermanagementapi.dto.request.UpdateUserRequest;
import com.george.usermanagementapi.dto.response.UserResponse;
import com.george.usermanagementapi.exception.ErrorResponse;
import com.george.usermanagementapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing all User Management endpoints under {@code /api/v1/users}.
 *
 * <p>This class is intentionally thin — it handles HTTP concerns only
 * (parsing, validation, status codes) and delegates all business logic to
 * {@link UserService}.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "CRUD operations for user accounts")
public class UserController {

    private final UserService userService;

    // ── Create ─────────────────────────────────────────────────────────────────

    /**
     * Creates a new user account.
     *
     * @param request validated request body
     * @return 201 Created with the new user payload
     */
    @PostMapping
    @Operation(
            summary = "Create a user",
            description = "Registers a new user. Email must be unique.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── Read (collection) ──────────────────────────────────────────────────────

    /**
     * Returns a paginated list of all users.
     *
     * <p>Supports standard Spring Pageable query parameters:
     * {@code page}, {@code size}, {@code sort} (e.g. {@code sort=lastName,asc}).
     *
     * @param pageable pagination and sort parameters — defaults to page 0,
     *                 size 10, sorted by {@code createdAt} descending
     * @return 200 OK with a page of user payloads
     */
    @GetMapping
    @Operation(
            summary = "List all users",
            description = "Returns a paginated list of users. "
                    + "Use ?page=0&size=10&sort=lastName,asc to control pagination and sorting.")
    @ApiResponse(responseCode = "200", description = "Users retrieved")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    // ── Read (single) ──────────────────────────────────────────────────────────

    /**
     * Retrieves a single user by their ID.
     *
     * @param id the user's primary key
     * @return 200 OK with the user payload, or 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID of the user to retrieve", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(userService.getUserById(id));
    }

    // ── Full update (PUT) ──────────────────────────────────────────────────────

    /**
     * Fully replaces all fields of an existing user.
     *
     * @param id      the ID of the user to update
     * @param request validated replacement payload
     * @return 200 OK with the updated user payload
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Fully update a user",
            description = "Replaces every field of the user. All required fields must be supplied.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID of the user to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    // ── Partial update (PATCH) ─────────────────────────────────────────────────

    /**
     * Partially updates an existing user — only fields present in the request
     * body are changed.
     *
     * @param id      the ID of the user to patch
     * @param request patch payload (all fields optional)
     * @return 200 OK with the patched user payload
     */
    @PatchMapping("/{id}")
    @Operation(
            summary = "Partially update a user",
            description = "Updates only the fields supplied in the request body. "
                    + "Fields omitted from the body are left unchanged.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User patched"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email already in use",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> patchUser(
            @Parameter(description = "ID of the user to patch", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PatchUserRequest request) {

        return ResponseEntity.ok(userService.patchUser(id, request));
    }

    // ── Delete ─────────────────────────────────────────────────────────────────

    /**
     * Permanently deletes a user.
     *
     * @param id the ID of the user to delete
     * @return 204 No Content on success
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Permanently removes the user record.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", example = "1")
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
