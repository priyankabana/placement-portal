package com.college.placement.dto;

import com.college.placement.model.UserRole;

/**
 * Whatever is kept in the HTTP session after a successful sign in.
 * Only these few values are stored - the password is never put in the session.
 */
public class LoggedInUser {

    private final UserRole role;
    private final Long studentId;      // filled only for a student
    private final String displayName;
    private final String username;
    private final String subtitle;     // designation or branch, shown in the sidebar

    public LoggedInUser(UserRole role, Long studentId, String displayName,
                        String username, String subtitle) {
        this.role = role;
        this.studentId = studentId;
        this.displayName = displayName;
        this.username = username;
        this.subtitle = subtitle;
    }

    /** Officer and faculty both manage the records. */
    public boolean isStaff() {
        return role == UserRole.STAFF;
    }

    public boolean isStudent() {
        return role == UserRole.STUDENT;
    }

    public UserRole getRole() {
        return role;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
