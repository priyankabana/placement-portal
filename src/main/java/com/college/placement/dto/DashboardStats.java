package com.college.placement.dto;

/**
 * Small read only carrier for the six tiles on the dashboard.
 *
 * A record is used instead of an entity because these numbers are calculated,
 * they are never stored in any table.
 */
public record DashboardStats(long totalStudents,
                             long totalCompanies,
                             long openDrives,
                             long totalApplications,
                             long placedStudents,
                             double highestPackage,
                             double averagePackage) {

    /** Placement percentage of the whole college, rounded to one decimal. */
    public double placementPercentage() {
        if (totalStudents == 0) {
            return 0.0;
        }
        return Math.round((placedStudents * 1000.0) / totalStudents) / 10.0;
    }

    public long unplacedStudents() {
        return totalStudents - placedStudents;
    }
}
