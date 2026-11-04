package com.college.placement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.college.placement.exception.DuplicateRecordException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.model.Student;
import com.college.placement.service.DepartmentService;
import com.college.placement.service.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/** Create, read, update and delete checked end to end through the service. */
@SpringBootTest
@Transactional
class StudentCrudTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private DepartmentService departmentService;

    @Test
    @DisplayName("a student can be saved, edited and then deleted")
    void studentCrudWorks() {
        Student student = new Student();
        student.setRollNumber("CS22099");
        student.setFirstName("Test");
        student.setLastName("Candidate");
        student.setEmail("test.candidate@campus.edu.in");
        student.setPhone("9000099099");
        student.setCgpa(7.5);
        student.setBatchYear(2026);
        student.setBacklogs(0);
        student.setDepartment(departmentService.getById(1L));

        Student saved = studentService.create(student);
        assertEquals("Test Candidate", saved.getFullName());

        Student edited = new Student();
        edited.setRollNumber("CS22099");
        edited.setFirstName("Test");
        edited.setLastName("Candidate");
        edited.setEmail("test.candidate@campus.edu.in");
        edited.setPhone("9000099099");
        edited.setCgpa(8.1);
        edited.setBatchYear(2026);
        edited.setBacklogs(0);
        edited.setDepartment(departmentService.getById(2L));

        Student updated = studentService.update(saved.getId(), edited);
        assertEquals(8.1, updated.getCgpa());
        assertEquals("IT", updated.getDepartment().getCode());
        assertFalse(updated.isPlaced());

        studentService.deleteById(saved.getId());
        assertThrows(ResourceNotFoundException.class, () -> studentService.getById(saved.getId()));
    }

    @Test
    @DisplayName("a roll number already present in the table is refused")
    void duplicateRollNumberIsRefused() {
        Student student = new Student();
        student.setRollNumber("CS22001");
        student.setFirstName("Copy");
        student.setLastName("Entry");
        student.setEmail("copy.entry@campus.edu.in");
        student.setCgpa(7.0);
        student.setBatchYear(2026);
        student.setBacklogs(0);
        student.setDepartment(departmentService.getById(1L));

        assertThrows(DuplicateRecordException.class, () -> studentService.create(student));
    }
}
