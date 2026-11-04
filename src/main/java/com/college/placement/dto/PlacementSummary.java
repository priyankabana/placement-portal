package com.college.placement.dto;

import com.college.placement.model.ApplicationStatus;
import java.util.List;
import java.util.Map;

/**
 * Body of GET /api/stats. Only counts and packages are returned, never a
 * student's personal details, so this endpoint can stay public.
 */
public record PlacementSummary(long totalStudents,
                               long totalCompanies,
                               long openDrives,
                               long totalApplications,
                               long placedStudents,
                               double placementPercentage,
                               double highestPackageLpa,
                               double averagePackageLpa,
                               Map<ApplicationStatus, Long> applicationsByStage,
                               List<BranchRow> branches) {

    public record BranchRow(String code, long totalStudents, long placedStudents,
                            double placementPercentage) {
    }
}
