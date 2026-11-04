package com.college.placement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.college.placement.model.Student;
import com.college.placement.repository.StudentRepository;
import com.college.placement.service.AuthService;
import com.college.placement.service.DepartmentService;
import com.college.placement.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/** Passwords are stored as BCrypt hashes and checked with PasswordEncoder.matches. */
@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentService departmentService;

    @Test
    @DisplayName("seeded staff and student accounts sign in with their demo passwords only")
    void seededAccountsUseHashedPasswords() {
        assertTrue(authService.loginStaff("placement.officer", "officer@123").isPresent());
        assertFalse(authService.loginStaff("placement.officer", "wrong-password").isPresent());
        assertTrue(authService.loginStudent("CS22001", "cs22001@2026").isPresent());
        assertFalse(authService.loginStudent("CS22001", "cs22001@2025").isPresent());
    }

    @Test
    @DisplayName("a newly registered student gets a hashed default password")
    void newStudentPasswordIsHashed() {
        Student student = new Student();
        student.setRollNumber("CS22999");
        student.setFirstName("Hash");
        student.setLastName("Check");
        student.setEmail("hash.check@campus.edu.in");
        student.setPhone("9000000000");
        student.setCgpa(7.5);
        student.setBatchYear(2026);
        student.setBacklogs(0);
        student.setDepartment(departmentService.getById(1L));
        studentService.create(student);

        String stored = studentRepository.findByRollNumberIgnoreCase("CS22999").orElseThrow().getPassword();
        assertTrue(stored.startsWith("$2"), "password should be stored as a BCrypt hash");
        assertTrue(authService.loginStudent("CS22999", "cs22999@2026").isPresent());
    }
}
