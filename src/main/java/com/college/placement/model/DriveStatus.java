package com.college.placement.model;

/**
 * Life cycle of a campus drive.
 * Stored as text in the DB (EnumType.STRING) so the rows stay readable.
 */
public enum DriveStatus {

    UPCOMING("Upcoming"),
    OPEN("Open for applications"),
    CLOSED("Closed");

    private final String label;

    DriveStatus(String label) {
        this.label = label;
    }

    /** Used by the JSP-like Thymeleaf pages to print a friendly name. */
    public String getLabel() {
        return label;
    }
}
