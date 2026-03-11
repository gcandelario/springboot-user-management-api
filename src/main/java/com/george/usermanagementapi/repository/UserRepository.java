package com.george.usermanagementapi.repository;

import com.george.usermanagementapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entities.
 *
 * <p>Extends {@link JpaRepository} to inherit standard CRUD and pagination
 * operations. Custom query methods are derived from their names by Spring.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their exact email address (case-sensitive).
     *
     * @param email the email address to search for
     * @return an {@link Optional} containing the user if found, or empty
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email address exists.
     *
     * @param email the email address to check
     * @return {@code true} if at least one user has this email
     */
    boolean existsByEmail(String email);

    /**
     * Checks whether any user OTHER than the one with {@code excludedId}
     * already owns the given email address. Used during PUT/PATCH to allow
     * a user to keep their own email while preventing duplicates.
     *
     * @param email      the email address to check
     * @param excludedId the ID of the user to exclude from the check
     * @return {@code true} if another user already has this email
     */
    boolean existsByEmailAndIdNot(String email, Long excludedId);
}
