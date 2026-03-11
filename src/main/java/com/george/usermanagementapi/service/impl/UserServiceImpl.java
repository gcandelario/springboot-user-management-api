package com.george.usermanagementapi.service.impl;

import com.george.usermanagementapi.dto.request.CreateUserRequest;
import com.george.usermanagementapi.dto.request.PatchUserRequest;
import com.george.usermanagementapi.dto.request.UpdateUserRequest;
import com.george.usermanagementapi.dto.response.UserResponse;
import com.george.usermanagementapi.entity.User;
import com.george.usermanagementapi.exception.EmailAlreadyExistsException;
import com.george.usermanagementapi.exception.ResourceNotFoundException;
import com.george.usermanagementapi.mapper.UserMapper;
import com.george.usermanagementapi.repository.UserRepository;
import com.george.usermanagementapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link UserService}.
 *
 * <p>All public methods are transactional. Read-only operations use
 * {@code readOnly = true} to allow the JPA provider and database driver to
 * apply read optimisations (e.g. no dirty-checking, read replicas).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * {@inheritDoc}
     *
     * <p>Rejects the request immediately if the email is already taken to
     * avoid relying solely on a database unique-constraint error, which would
     * produce an unhelpful 500 response.
     */
    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);

        log.info("User created successfully with id: {}", saved.getId());
        return userMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates pagination and sorting entirely to Spring Data so the
     * controller can pass any {@link Pageable} parameters without changes here.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Fetching users — page {}, size {}, sort {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user with id: {}", id);
        return userMapper.toResponse(findUserOrThrow(id));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Allows a user to keep their existing email — the uniqueness check
     * excludes the current user's own ID via
     * {@code existsByEmailAndIdNot}.
     */
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Fully updating user with id: {}", id);

        User user = findUserOrThrow(id);

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        userMapper.updateEntity(user, request);
        User updated = userRepository.save(user);

        log.info("User {} fully updated", id);
        return userMapper.toResponse(updated);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The email uniqueness check is skipped entirely when no new email is
     * supplied in the patch request.
     */
    @Override
    @Transactional
    public UserResponse patchUser(Long id, PatchUserRequest request) {
        log.info("Patching user with id: {}", id);

        User user = findUserOrThrow(id);

        if (request.getEmail() != null
                && userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        userMapper.patchEntity(user, request);
        User updated = userRepository.save(user);

        log.info("User {} patched", id);
        return userMapper.toResponse(updated);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        User user = findUserOrThrow(id);
        userRepository.delete(user);
        log.info("User {} deleted", id);
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    /**
     * Looks up a user by ID or throws {@link ResourceNotFoundException}.
     *
     * @param id the user's primary key
     * @return the managed {@link User} entity
     */
    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
