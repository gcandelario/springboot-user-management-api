package com.george.usermanagementapi.exception;

/**
 * Thrown when a caller attempts to create or update a user with an email
 * address that is already registered to another account.
 *
 * <p>The global exception handler maps this to HTTP 409 Conflict.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * @param email the duplicate email address that triggered the conflict
     */
    public EmailAlreadyExistsException(String email) {
        super(String.format("A user with email '%s' already exists", email));
    }
}
