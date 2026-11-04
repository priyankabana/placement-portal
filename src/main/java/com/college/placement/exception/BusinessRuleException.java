package com.college.placement.exception;

/**
 * Parent of the "rule of the placement cell was broken" errors.
 *
 * Controllers catch this single type and show the message on top of the form,
 * instead of writing a separate catch block for every rule.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
