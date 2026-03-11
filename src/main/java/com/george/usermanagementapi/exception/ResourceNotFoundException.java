package com.george.usermanagementapi.exception;

/**
 * Thrown when a requested resource cannot be found in the database.
 *
 * <p>The global exception handler maps this to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates the exception with a fully custom message.
     *
     * @param message description of what was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Convenience constructor that produces a standard
     * "{@code resourceName} not found with id: {@code id}" message.
     *
     * @param resourceName human-readable entity name (e.g. "User")
     * @param id           the ID that was looked up
     */
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with id: %d", resourceName, id));
    }
}
