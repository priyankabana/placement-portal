package com.college.placement.model;

/**
 * Who is using the portal.
 *
 * The placement officer and the department faculty share one role (STAFF) - both
 * of them manage the placement records. A student is a separate role with a much
 * smaller access.
 */
public enum UserRole {

    /** Placement officer and faculty - full rights on every module. */
    STAFF("Faculty / Officer"),

    /** Student - own profile, the recruiter list, the drives and self apply. */
    STUDENT("Student");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
