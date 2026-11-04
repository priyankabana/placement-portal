package com.college.placement.dto;

import com.college.placement.model.Drive;
import com.college.placement.model.DriveStatus;
import java.time.LocalDate;

/**
 * JSON view of a drive for the read only API.
 *
 * The entity itself is not returned, because it holds the list of applications
 * (student names and remarks) and a lazy collection would be serialised with it.
 */
public record DriveSummary(Long id,
                           String company,
                           String jobRole,
                           Double packageLpa,
                           Double minCgpa,
                           Integer maxBacklogs,
                           LocalDate driveDate,
                           String location,
                           DriveStatus status) {

    public static DriveSummary from(Drive drive) {
        return new DriveSummary(
                drive.getId(),
                drive.getCompany().getName(),
                drive.getJobRole(),
                drive.getPackageLpa(),
                drive.getMinCgpa(),
                drive.getMaxBacklogs(),
                drive.getDriveDate(),
                drive.getLocation(),
                drive.getStatus());
    }
}
