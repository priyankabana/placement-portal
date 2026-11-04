package com.college.placement.exception;

/**
 * Thrown when a URL carries an id that is not present in the database,
 * for example /students/999/edit after that student was deleted.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
