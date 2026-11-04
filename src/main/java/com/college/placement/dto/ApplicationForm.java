package com.college.placement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Backing object of the "new application" form.
 *
 * A separate form class is used here instead of the Application entity, because
 * the page only collects two ids and a date - the service then loads the real
 * student and drive rows and applies the eligibility rules.
 */
public class ApplicationForm {

    @NotNull(message = "Please choose a student")
    private Long studentId;

    @NotNull(message = "Please choose a drive")
    private Long driveId;

    @NotNull(message = "Applied date is required")
    private LocalDate appliedOn = LocalDate.now();

    @Size(max = 200, message = "Remark should be within 200 characters")
    private String remarks;

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getDriveId() {
        return driveId;
    }

    public void setDriveId(Long driveId) {
        this.driveId = driveId;
    }

    public LocalDate getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(LocalDate appliedOn) {
        this.appliedOn = appliedOn;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
