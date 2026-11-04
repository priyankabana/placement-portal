package com.college.placement.dto;

/**
 * One row of the branch wise report table.
 * Built in the service layer from two count queries.
 */
public record DepartmentPlacementStat(String departmentCode,
                                      String departmentName,
                                      long totalStudents,
                                      long placedStudents) {

    public double placementPercentage() {
        if (totalStudents == 0) {
            return 0.0;
        }
        return Math.round((placedStudents * 1000.0) / totalStudents) / 10.0;
    }
}
