package com.college.placement.service;

import com.college.placement.dto.LoggedInUser;
import com.college.placement.model.Staff;
import com.college.placement.model.Student;
import com.college.placement.model.UserRole;
import com.college.placement.repository.StaffRepository;
import com.college.placement.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Handles the two sign in tabs of the login page.
 *
 *  - Faculty / Officer tab : checked against the staff table
 *  - Student tab           : roll number is the user id, password is the one
 *                            stored in the student table
 *
 * Both tabs are kept separate on purpose, so a student cannot try a faculty
 * user id and find out whether it exists.
 *
 * Passwords are stored as BCrypt hashes (see PasswordConfig). The typed
 * password is checked with PasswordEncoder.matches, so the plain text never
 * reaches the database or the session.
 */
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final StaffRepository staffRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(StaffRepository staffRepository, StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Faculty and placement officer sign in. */
    public Optional<LoggedInUser> loginStaff(String username, String password) {
        if (isBlank(username, password)) {
            return Optional.empty();
        }
        return staffRepository.findByUsernameIgnoreCase(username.trim())
                .filter(staff -> passwordMatches(password, staff.getPassword()))
                .map(this::toSessionUser);
    }

    /** Student sign in with the roll number. */
    public Optional<LoggedInUser> loginStudent(String rollNumber, String password) {
        if (isBlank(rollNumber, password)) {
            return Optional.empty();
        }
        return studentRepository.findByRollNumberIgnoreCase(rollNumber.trim())
                .filter(student -> passwordMatches(password, student.getPassword()))
                .map(student -> new LoggedInUser(
                        UserRole.STUDENT,
                        student.getId(),
                        student.getFullName(),
                        student.getRollNumber(),
                        student.getDepartment().getCode() + " - Batch " + student.getBatchYear()));
    }

    /** Staff list printed on the login page hint box and in the credentials file. */
    public List<Staff> findAllStaff() {
        return staffRepository.findAllByOrderByNameAsc();
    }

    /**
     * First password of a student - roll number in small letters, @ and the
     * passing out year, for example cs22001@2026.
     *
     * A fixed rule is used so that the officer can always tell a student what to
     * type, and so that the credentials list can be printed for the whole batch.
     */
    public static String defaultPasswordFor(Student student) {
        return student.getRollNumber().toLowerCase() + "@" + student.getBatchYear();
    }

    private LoggedInUser toSessionUser(Staff staff) {
        String subtitle = staff.getDepartment() != null
                ? staff.getDesignation() + ", " + staff.getDepartment().getCode()
                : staff.getDesignation();
        return new LoggedInUser(staff.getRole(), null, staff.getName(),
                staff.getUsername(), subtitle);
    }

    private boolean passwordMatches(String rawPassword, String storedHash) {
        return storedHash != null && passwordEncoder.matches(rawPassword, storedHash);
    }

    private boolean isBlank(String username, String password) {
        return !StringUtils.hasText(username) || !StringUtils.hasText(password);
    }
}
