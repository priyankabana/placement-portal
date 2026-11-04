package com.college.placement.controller;

import com.college.placement.exception.BusinessRuleException;
import com.college.placement.model.Department;
import com.college.placement.model.Student;
import com.college.placement.service.DepartmentService;
import com.college.placement.service.StudentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Full CRUD screens for students.
 *
 * URL plan followed by every module of this project:
 *   GET  /students             list + search
 *   GET  /students/new         blank form           (Create)
 *   GET  /students/{id}        profile page         (Read)
 *   GET  /students/{id}/edit   filled form          (Update)
 *   POST /students/save        insert or update
 *   POST /students/{id}/delete remove               (Delete)
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final DepartmentService departmentService;

    public StudentController(StudentService studentService, DepartmentService departmentService) {
        this.studentService = studentService;
        this.departmentService = departmentService;
    }

    /** Every page of this controller needs the branch list for the dropdown/filter. */
    @ModelAttribute("departments")
    public List<Department> departments() {
        return departmentService.findAll();
    }

    /** Highlights the correct link in the sidebar. */
    @ModelAttribute("activePage")
    public String activePage() {
        return "students";
    }

    @GetMapping
    public String listStudents(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Long departmentId,
                               Model model) {
        model.addAttribute("students", studentService.search(keyword, departmentId));
        // sent back to the view so the search box keeps whatever was typed
        model.addAttribute("keyword", keyword);
        model.addAttribute("departmentId", departmentId);
        return "students/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("formTitle", "Register new student");
        return "students/form";
    }

    @GetMapping("/{id}")
    public String showProfile(@PathVariable Long id, Model model) {
        Student student = studentService.getById(id);
        model.addAttribute("student", student);
        model.addAttribute("applications", studentService.findApplicationsOf(id));
        return "students/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getById(id));
        model.addAttribute("formTitle", "Edit student details");
        return "students/form";
    }

    /**
     * One handler for insert and update - a filled hidden id means update.
     * @Valid runs the annotations written on the entity; if anything fails we
     * return to the same form and Thymeleaf prints the messages.
     */
    @PostMapping("/save")
    public String saveStudent(@Valid @ModelAttribute("student") Student student,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        boolean isNew = student.getId() == null;

        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", isNew ? "Register new student" : "Edit student details");
            return "students/form";
        }

        try {
            Student saved = isNew
                    ? studentService.create(student)
                    : studentService.update(student.getId(), student);

            redirectAttributes.addFlashAttribute("successMessage",
                    saved.getFullName() + (isNew ? " was registered." : " was updated."));
            return "redirect:/students/" + saved.getId();

        } catch (BusinessRuleException exception) {
            // duplicate roll number or duplicate email - show it above the form
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("formTitle", isNew ? "Register new student" : "Edit student details");
            return "students/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Student student = studentService.getById(id);
        studentService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage",
                student.getFullName() + " was removed along with the application history.");
        return "redirect:/students";
    }
}
