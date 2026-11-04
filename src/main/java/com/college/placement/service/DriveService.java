package com.college.placement.service;

import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.model.Application;
import com.college.placement.model.Drive;
import com.college.placement.model.DriveStatus;
import com.college.placement.model.Student;
import com.college.placement.repository.ApplicationRepository;
import com.college.placement.repository.DriveRepository;
import com.college.placement.repository.StudentRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handles the campus drives - creation, status changes and the eligibility list.
 */
@Service
@Transactional(readOnly = true)
public class DriveService {

    private final DriveRepository driveRepository;
    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;

    public DriveService(DriveRepository driveRepository,
                        StudentRepository studentRepository,
                        ApplicationRepository applicationRepository) {
        this.driveRepository = driveRepository;
        this.studentRepository = studentRepository;
        this.applicationRepository = applicationRepository;
    }

    public List<Drive> findAll() {
        return driveRepository.findAllWithCompany();
    }

    public List<Drive> findOpenDrives() {
        return driveRepository.findByStatusOrderByDriveDateAsc(DriveStatus.OPEN);
    }

    /** Next drives from today onwards - the dashboard shows the first few of these. */
    public List<Drive> findUpcoming() {
        return driveRepository.findUpcoming(LocalDate.now(), DriveStatus.CLOSED);
    }

    public List<Drive> findByCompany(Long companyId) {
        return driveRepository.findByCompanyIdOrderByDriveDateDesc(companyId);
    }

    public Drive getById(Long id) {
        return driveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No drive found with id " + id));
    }

    public List<Application> findApplicationsOf(Long driveId) {
        return applicationRepository.findByDriveIdOrderByStatusAscIdAsc(driveId);
    }

    /**
     * Students who clear the CGPA and backlog condition of this drive and have
     * not applied yet - shown as "eligible students" on the drive page.
     */
    public List<Student> findEligibleStudents(Drive drive) {
        return studentRepository.findEligibleStudents(
                drive.getMinCgpa(), drive.getMaxBacklogs(), drive.getId());
    }

    public long countOpenDrives() {
        return driveRepository.countByStatus(DriveStatus.OPEN);
    }

    public long count() {
        return driveRepository.count();
    }

    @Transactional
    public Drive create(Drive drive) {
        return driveRepository.save(drive);
    }

    @Transactional
    public Drive update(Long id, Drive formData) {
        Drive existing = getById(id);

        existing.setCompany(formData.getCompany());
        existing.setJobRole(formData.getJobRole());
        existing.setPackageLpa(formData.getPackageLpa());
        existing.setMinCgpa(formData.getMinCgpa());
        existing.setMaxBacklogs(formData.getMaxBacklogs());
        existing.setDriveDate(formData.getDriveDate());
        existing.setLocation(formData.getLocation());
        existing.setDescription(formData.getDescription());
        existing.setStatus(formData.getStatus());

        return driveRepository.save(existing);
    }

    /** Quick status switch from the drive list, without opening the full form. */
    @Transactional
    public Drive changeStatus(Long id, DriveStatus status) {
        Drive drive = getById(id);
        drive.setStatus(status);
        return driveRepository.save(drive);
    }

    @Transactional
    public void deleteById(Long id) {
        driveRepository.delete(getById(id));
    }
}
