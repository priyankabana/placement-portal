package com.college.placement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.college.placement.exception.DuplicateRecordException;
import com.college.placement.exception.EligibilityNotMetException;
import com.college.placement.model.Application;
import com.college.placement.model.ApplicationStatus;
import com.college.placement.model.Drive;
import com.college.placement.model.Student;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.DepartmentService;
import com.college.placement.service.DriveService;
import com.college.placement.service.ReportService;
import com.college.placement.service.StudentService;
import java.time.LocalDate;
import java.util.List;
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

    @Autowired
    private DriveService driveService;

    @Autowired
    private DepartmentService departmentService;

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

    @Test
    @DisplayName("a student with more backlogs than the drive allows is refused")
    void tooManyBacklogsIsRejected() {
        // CGPA clears the 7.0 cut off of drive 6, but drive 6 allows 0 backlogs
        Student student = studentService.create(newStudent("CS22097", 8.5, 2));

        EligibilityNotMetException error = assertThrows(EligibilityNotMetException.class,
                () -> applicationService.create(student.getId(), 6L, LocalDate.now(), null));
        assertTrue(error.getMessage().contains("backlog"));
    }

    @Test
    @DisplayName("a closed or upcoming drive does not accept applications")
    void onlyOpenDrivesAcceptApplications() {
        // student 3 (9.1 CGPA, no backlogs) clears every cut off
        assertThrows(EligibilityNotMetException.class,
                () -> applicationService.create(3L, 1L, LocalDate.now(), null));   // CLOSED
        assertThrows(EligibilityNotMetException.class,
                () -> applicationService.create(3L, 10L, LocalDate.now(), null));  // UPCOMING
    }

    @Test
    @DisplayName("CGPA equal to the cut off is accepted and a selection counts as placed")
    void cutOffIsInclusiveAndSelectionPlacesStudent() {
        Student student = studentService.create(newStudent("CS22098", 7.0, 0));
        long placedBefore = reportService.buildDashboardStats().placedStudents();

        Application application = applicationService.create(student.getId(), 6L, null, null);
        assertEquals(ApplicationStatus.APPLIED, application.getStatus());
        assertEquals(LocalDate.now(), application.getAppliedOn());

        applicationService.updateStatus(application.getId(), ApplicationStatus.SELECTED, "Offer made");
        assertEquals(placedBefore + 1, reportService.buildDashboardStats().placedStudents());
    }

    @Test
    @DisplayName("eligible list of a drive meets both cut offs and skips students who applied")
    void eligibleListMatchesTheRules() {
        Drive drive = driveService.getById(6L);   // 7.0 CGPA, 0 backlogs
        List<Student> eligible = driveService.findEligibleStudents(drive);

        assertFalse(eligible.isEmpty());
        for (Student student : eligible) {
            assertTrue(student.getCgpa() >= drive.getMinCgpa());
            assertTrue(student.getBacklogs() <= drive.getMaxBacklogs());
        }
        // students 1, 13 and 18 already applied to drive 6 in data.sql
        List<Long> ids = eligible.stream().map(Student::getId).toList();
        assertFalse(ids.contains(1L));
        assertFalse(ids.contains(13L));
        assertFalse(ids.contains(18L));
    }

    private Student newStudent(String rollNumber, double cgpa, int backlogs) {
        Student student = new Student();
        student.setRollNumber(rollNumber);
        student.setFirstName("Test");
        student.setLastName(rollNumber);
        student.setEmail(rollNumber.toLowerCase() + "@campus.edu.in");
        student.setPhone("9000099097");
        student.setCgpa(cgpa);
        student.setBatchYear(2026);
        student.setBacklogs(backlogs);
        student.setDepartment(departmentService.getById(1L));
        return student;
    }
}
