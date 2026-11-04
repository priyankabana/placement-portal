package com.college.placement.controller;

import com.college.placement.service.ApplicationService;
import com.college.placement.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Read only reporting screen - branch wise placement percentage, the stage wise
 * funnel and the final offer list.
 */
@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final ApplicationService applicationService;

    public ReportController(ReportService reportService, ApplicationService applicationService) {
        this.reportService = reportService;
        this.applicationService = applicationService;
    }

    @GetMapping
    public String showReports(Model model) {
        model.addAttribute("stats", reportService.buildDashboardStats());
        model.addAttribute("departmentStats", reportService.buildDepartmentStats());
        model.addAttribute("statusCounts", reportService.countByEveryStatus());
        model.addAttribute("busiestStage", reportService.busiestStageCount());
        model.addAttribute("offers", applicationService.findOffers());
        model.addAttribute("activePage", "reports");
        return "reports";
    }
}
