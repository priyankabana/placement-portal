package com.college.placement.controller;

import com.college.placement.dto.DashboardStats;
import com.college.placement.dto.DriveSummary;
import com.college.placement.dto.PlacementSummary;
import com.college.placement.model.DriveStatus;
import com.college.placement.service.DriveService;
import com.college.placement.service.ReportService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Small read only JSON API next to the Thymeleaf pages.
 *
 * It reuses the same services as the HTML screens, so the numbers on the
 * dashboard and the numbers here always come from one place.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final DriveService driveService;
    private final ReportService reportService;

    public ApiController(DriveService driveService, ReportService reportService) {
        this.driveService = driveService;
        this.reportService = reportService;
    }

    /** All drives, latest first. {@code ?status=OPEN} narrows the list. */
    @GetMapping("/drives")
    public List<DriveSummary> drives(@RequestParam(required = false) DriveStatus status) {
        return driveService.findAll().stream()
                .filter(drive -> status == null || drive.getStatus() == status)
                .map(DriveSummary::from)
                .toList();
    }

    @GetMapping("/stats")
    public PlacementSummary stats() {
        DashboardStats stats = reportService.buildDashboardStats();
        List<PlacementSummary.BranchRow> branches = reportService.buildDepartmentStats().stream()
                .map(row -> new PlacementSummary.BranchRow(row.departmentCode(),
                        row.totalStudents(), row.placedStudents(), row.placementPercentage()))
                .toList();

        return new PlacementSummary(
                stats.totalStudents(),
                stats.totalCompanies(),
                stats.openDrives(),
                stats.totalApplications(),
                stats.placedStudents(),
                stats.placementPercentage(),
                stats.highestPackage(),
                stats.averagePackage(),
                reportService.countByEveryStatus(),
                branches);
    }

    /**
     * A wrong value such as {@code ?status=DONE} is the caller's mistake, so it
     * gets a 400 with a JSON body instead of the HTML error page.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badParameter(MethodArgumentTypeMismatchException exception) {
        return Map.of("error", "Invalid value '" + exception.getValue()
                + "' for parameter '" + exception.getName() + "'");
    }
}
