package com.college.placement.service;

import com.college.placement.exception.DuplicateRecordException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.model.Application;
import com.college.placement.model.Student;
import com.college.placement.repository.ApplicationRepository;
import com.college.placement.repository.StudentRepository;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * All student related rules sit here - the controller only forwards form data
 * and the repository only talks to the database.
 */
@Service
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentService(StudentRepository studentRepository,
                          ApplicationRepository applicationRepository,
                          PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.applicationRepository = applicationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ------------------------------------------------- read side (the R of CRUD) */

    public List<Student> findAll() {
        return studentRepository.findAllByOrderByRollNumberAsc();
    }

    /**
     * Search used by the student list page. A blank box is treated as "match all"
     * because the query does a LIKE '%%' in that case.
     */
    public List<Student> search(String keyword, Long departmentId) {
        String safeKeyword = StringUtils.hasText(keyword) ? keyword.trim() : "";
        return studentRepository.search(safeKeyword, departmentId);
    }

    public Student getById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No student found with id " + id));
    }

    /** Application history shown on the student profile page. */
    public List<Application> findApplicationsOf(Long studentId) {
        return applicationRepository.findByStudentIdOrderByAppliedOnDesc(studentId);
    }

    public long count() {
        return studentRepository.count();
    }

    /* ------------------------------------------- write side (the C, U and D part) */

    @Transactional
    public Student create(Student student) {
        checkRollNumberIsFree(student);
        checkEmailIsFree(student);

        // the officer's form has no password box, so the login password is
        // generated here from the roll number and the batch year, then hashed
        String rawPassword = StringUtils.hasText(student.getPassword())
                ? student.getPassword()
                : AuthService.defaultPasswordFor(student);
        student.setPassword(passwordEncoder.encode(rawPassword));
        return studentRepository.save(student);
    }

    /**
     * Copies the editable fields on to the row already present in the database.
     *
     * The form object cannot be saved directly: it carries an empty application
     * list, and saving that would wipe the student's real application history.
     */
    @Transactional
    public Student update(Long id, Student formData) {
        Student existing = getById(id);

        formData.setId(id);
        checkRollNumberIsFree(formData);
        checkEmailIsFree(formData);

        existing.setRollNumber(formData.getRollNumber());
        existing.setFirstName(formData.getFirstName());
        existing.setLastName(formData.getLastName());
        existing.setEmail(formData.getEmail());
        existing.setPhone(formData.getPhone());
        existing.setCgpa(formData.getCgpa());
        existing.setBatchYear(formData.getBatchYear());
        existing.setBacklogs(formData.getBacklogs());
        existing.setResumeUrl(formData.getResumeUrl());
        // password is not on the form, so the stored one is left untouched
        existing.setDepartment(formData.getDepartment());

        return studentRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        Student student = getById(id);
        // cascade + orphanRemoval on the entity clears the child applications as well
        studentRepository.delete(student);
    }

    /* ------------------------------------------------------------ internal rules */

    private void checkRollNumberIsFree(Student student) {
        studentRepository.findByRollNumberIgnoreCase(student.getRollNumber())
                .filter(other -> !other.getId().equals(student.getId()))
                .ifPresent(other -> {
                    throw new DuplicateRecordException(
                            "Roll number " + student.getRollNumber()
                                    + " is already registered for " + other.getFullName() + ".");
                });
    }

    private void checkEmailIsFree(Student student) {
        studentRepository.findByEmailIgnoreCase(student.getEmail())
                .filter(other -> !other.getId().equals(student.getId()))
                .ifPresent(other -> {
                    throw new DuplicateRecordException(
                            "Email " + student.getEmail() + " is already used by "
                                    + other.getFullName() + ".");
                });
    }
}
