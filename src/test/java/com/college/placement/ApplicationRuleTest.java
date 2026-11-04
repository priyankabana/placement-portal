package com.college.placement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.college.placement.exception.DuplicateRecordException;
import com.college.placement.exception.EligibilityNotMetException;
import com.college.placement.model.ApplicationStatus;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.ReportService;
import com.college.placement.service.StudentService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests run against the sample data loaded from data.sql.
 *
 * Student 1 (Aditi, 8.6 CGPA) has already applied to drive 1, and student 6
 * (Vishal, 6.2 CGPA with 2 backlogs) fails the cut off of drive 3.
 */
@SpringBootTest
@Transactional
class ApplicationRuleTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ReportService reportService;

    @Test
    @DisplayName("the same student cannot apply to one drive twice")
    void duplicateApplicationIsRejected() {
        // drive 6 is open and student 1 already has a row for it in data.sql
        assertThrows(DuplicateRecordException.class,
                () -> applicationService.create(1L, 6L, LocalDate.now(), null));
    }

    @Test
    @DisplayName("a student below the CGPA cut off is not allowed to apply")
    void lowCgpaApplicationIsRejected() {
        assertThrows(EligibilityNotMetException.class,
                () -> applicationService.create(6L, 3L, LocalDate.now(), null));
    }

    @Test
    @DisplayName("selected applications decide the placed count")
    void placedCountMatchesSelectedApplications() {
        long selected = applicationService.countByStatus(ApplicationStatus.SELECTED);
        assertEquals(8, selected);
        assertTrue(studentService.getById(1L).isPlaced());
        assertEquals(8, reportService.buildDashboardStats().placedStudents());
    }
}
