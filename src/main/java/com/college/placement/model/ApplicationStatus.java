package com.college.placement.model;

/**
 * Stage of a student's application inside one drive.
 * SELECTED is what the reports treat as "placed".
 */
public enum ApplicationStatus {

    APPLIED("Applied"),
    SHORTLISTED("Shortlisted"),
    SELECTED("Selected"),
    REJECTED("Rejected");

    private final String label;

    ApplicationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
