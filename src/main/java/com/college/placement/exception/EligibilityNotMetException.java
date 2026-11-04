package com.college.placement.exception;

/**
 * Thrown when a student does not clear the CGPA / backlog bar of a drive,
 * or when the drive is not open for applications any more.
 */
public class EligibilityNotMetException extends BusinessRuleException {

    public EligibilityNotMetException(String message) {
        super(message);
    }
}
