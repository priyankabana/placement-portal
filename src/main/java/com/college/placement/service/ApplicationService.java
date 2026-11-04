package com.college.placement.service;

import com.college.placement.exception.DuplicateRecordException;
import com.college.placement.exception.EligibilityNotMetException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.model.Application;
import com.college.placement.model.ApplicationStatus;
import com.college.placement.model.Drive;
import com.college.placement.model.Student;
import com.college.placement.repository.ApplicationRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The core of the project - it connects a student to a drive and moves that
 * application through Applied -> Shortlisted -> Selected / Rejected.
 *
 * Two college rules are enforced before an application is stored:
 *   1. the drive must still be open, and the student must clear CGPA + backlogs
 *   2. the same student cannot apply to the same drive twice
 */
@Service
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentService studentService;
    private final DriveService driveService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              StudentService studentService,
                              DriveService driveService) {
        this.applicationRepository = applicationRepository;
        this.studentService = studentService;
        this.driveService = driveService;
    }

    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    public List<Application> findByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatusOrderByAppliedOnDesc(status);
    }

    public List<Application> findRecent() {
        return applicationRepository.findTop6ByOrderByAppliedOnDescIdDesc();
    }

    /** Offer letter list for the reports page, costliest package first. */
    public List<Application> findOffers() {
        return applicationRepository.findOffers(ApplicationStatus.SELECTED);
    }

    public Application getById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No application found with id " + id));
    }

    public long count() {
        return applicationRepository.count();
    }

    public long countByStatus(ApplicationStatus status) {
        return applicationRepository.countByStatus(status);
    }

    @Transactional
    public Application create(Long studentId, Long driveId, LocalDate appliedOn, String remarks) {
        Student student = studentService.getById(studentId);
        Drive drive = driveService.getById(driveId);

        checkNotAlreadyApplied(studentId, driveId);
        checkEligibility(student, drive);

        Application application = new Application();
        application.setStudent(student);
        application.setDrive(drive);
        application.setAppliedOn(appliedOn != null ? appliedOn : LocalDate.now());
        application.setStatus(ApplicationStatus.APPLIED);
        application.setRemarks(remarks);

        return applicationRepository.save(application);
    }

    /**
     * The placement coordinator only edits the stage and the remark after the
     * interview, so the student and the drive of an application never change.
     */
    @Transactional
    public Application updateStatus(Long id, ApplicationStatus status, String remarks) {
        Application application = getById(id);
        application.setStatus(status);
        application.setRemarks(remarks);
        return applicationRepository.save(application);
    }

    @Transactional
    public void deleteById(Long id) {
        applicationRepository.delete(getById(id));
    }

    /* ------------------------------------------------------------ college rules */

    private void checkNotAlreadyApplied(Long studentId, Long driveId) {
        if (applicationRepository.existsByStudentIdAndDriveId(studentId, driveId)) {
            throw new DuplicateRecordException(
                    "This student has already applied to the selected drive.");
        }
    }

    private void checkEligibility(Student student, Drive drive) {
        if (!drive.isAcceptingApplications()) {
            throw new EligibilityNotMetException(
                    "Drive of " + drive.getCompany().getName()
                            + " is " + drive.getStatus().getLabel().toLowerCase()
                            + ", so new applications cannot be added.");
        }
        if (student.getCgpa() < drive.getMinCgpa()) {
            throw new EligibilityNotMetException(
                    student.getFullName() + " has " + student.getCgpa()
                            + " CGPA but this drive needs at least " + drive.getMinCgpa() + ".");
        }
        if (student.getBacklogs() > drive.getMaxBacklogs()) {
            throw new EligibilityNotMetException(
                    student.getFullName() + " has " + student.getBacklogs()
                            + " backlog(s) and the drive allows only " + drive.getMaxBacklogs() + ".");
        }
    }
}
