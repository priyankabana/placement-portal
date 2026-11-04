package com.college.placement.exception;

/**
 * Thrown when a uniqueness rule is broken - a repeated roll number, a repeated
 * company name, or the same student applying to one drive twice.
 */
public class DuplicateRecordException extends BusinessRuleException {

    public DuplicateRecordException(String message) {
        super(message);
    }
}
