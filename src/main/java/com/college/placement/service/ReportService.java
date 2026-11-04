package com.college.placement.service;

import com.college.placement.dto.DashboardStats;
import com.college.placement.dto.DepartmentPlacementStat;
import com.college.placement.model.ApplicationStatus;
import com.college.placement.model.Department;
import com.college.placement.repository.ApplicationRepository;
import com.college.placement.repository.CompanyRepository;
import com.college.placement.repository.StudentRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read only service that prepares the numbers for the dashboard and the
 * reports page. Nothing here is stored back in the database.
 */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;
    private final DepartmentService departmentService;
    private final DriveService driveService;

    public ReportService(StudentRepository studentRepository,
                         CompanyRepository companyRepository,
                         ApplicationRepository applicationRepository,
                         DepartmentService departmentService,
                         DriveService driveService) {
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.applicationRepository = applicationRepository;
        this.departmentService = departmentService;
        this.driveService = driveService;
    }

    /** Six tiles at the top of the dashboard. */
    public DashboardStats buildDashboardStats() {
        long placed = applicationRepository.countPlacedStudents(ApplicationStatus.SELECTED);

        // max() and avg() return null when no offer exists yet, so default to zero
        Double highest = applicationRepository.findHighestPackage(ApplicationStatus.SELECTED);
        Double average = applicationRepository.findAveragePackage(ApplicationStatus.SELECTED);

        return new DashboardStats(
                studentRepository.count(),
                companyRepository.count(),
                driveService.countOpenDrives(),
                applicationRepository.count(),
                placed,
                highest == null ? 0.0 : round(highest),
                average == null ? 0.0 : round(average));
    }

    /**
     * Branch wise table on the reports page - total students against the number
     * of students who have at least one SELECTED application.
     */
    public List<DepartmentPlacementStat> buildDepartmentStats() {
        List<DepartmentPlacementStat> rows = new ArrayList<>();

        for (Department department : departmentService.findAll()) {
            long total = studentRepository.countByDepartmentId(department.getId());
            long placed = applicationRepository.countPlacedStudentsByDepartment(
                    ApplicationStatus.SELECTED, department.getId());
            rows.add(new DepartmentPlacementStat(
                    department.getCode(), department.getName(), total, placed));
        }
        return rows;
    }

    /**
     * Applied / Shortlisted / Selected / Rejected counts for the funnel bars.
     * A LinkedHashMap keeps the four stages in their natural order on the page.
     */
    public Map<ApplicationStatus, Long> countByEveryStatus() {
        Map<ApplicationStatus, Long> counts = new LinkedHashMap<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            counts.put(status, applicationRepository.countByStatus(status));
        }
        return counts;
    }

    /** Longest bar of the funnel, used to work out the width of the other bars. */
    public long busiestStageCount() {
        return countByEveryStatus().values().stream().mapToLong(Long::longValue).max().orElse(0L);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
