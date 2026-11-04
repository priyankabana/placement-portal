package com.college.placement.controller;

import com.college.placement.service.ApplicationService;
import com.college.placement.service.DriveService;
import com.college.placement.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home page of the portal. It only collects data from the services and hands it
 * over to the Thymeleaf template - no business logic is written here.
 */
@Controller
public class DashboardController {

    private final ReportService reportService;
    private final DriveService driveService;
    private final ApplicationService applicationService;

    public DashboardController(ReportService reportService,
                               DriveService driveService,
                               ApplicationService applicationService) {
        this.reportService = reportService;
        this.driveService = driveService;
        this.applicationService = applicationService;
    }

    @GetMapping("/")
    public String showDashboard(Model model) {
        model.addAttribute("stats", reportService.buildDashboardStats());
        model.addAttribute("departmentStats", reportService.buildDepartmentStats());
        model.addAttribute("upcomingDrives", driveService.findUpcoming());
        model.addAttribute("recentApplications", applicationService.findRecent());
        model.addAttribute("activePage", "dashboard");
        return "index";
    }

}
