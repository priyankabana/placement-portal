package com.college.placement.controller;

import com.college.placement.dto.LoggedInUser;
import com.college.placement.exception.BusinessRuleException;
import com.college.placement.model.Application;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.DriveService;
import com.college.placement.service.StudentService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The student side of the portal - own profile, own applications and applying to
 * a drive without waiting for the placement cell to do it.
 *
 * The student id is always taken from the session, never from the URL or a
 * hidden field, so one student can never apply on behalf of another student.
 */
@Controller
public class StudentPortalController {

    private final StudentService studentService;
    private final DriveService driveService;
    private final ApplicationService applicationService;

    public StudentPortalController(StudentService studentService,
                                   DriveService driveService,
                                   ApplicationService applicationService) {
        this.studentService = studentService;
        this.driveService = driveService;
        this.applicationService = applicationService;
    }

    @GetMapping("/my-profile")
    public String showMyProfile(HttpSession session, Model model) {
        LoggedInUser user = currentUser(session);

        if (user.isStaff()) {
            return "redirect:/";
        }

        Long studentId = user.getStudentId();
        List<Application> myApplications = studentService.findApplicationsOf(studentId);

        model.addAttribute("student", studentService.getById(studentId));
        model.addAttribute("applications", myApplications);
        model.addAttribute("openDrives", driveService.findOpenDrives());

        // drive ids already applied to, so the Apply button is not shown twice
        model.addAttribute("appliedDriveIds",
                myApplications.stream().map(application -> application.getDrive().getId()).toList());

        model.addAttribute("activePage", "my-profile");
        return "portal/my-profile";
    }

    /**
     * Student applies to a drive by themselves.
     *
     * The same ApplicationService is used as the officer's screen, so the drive
     * status, the CGPA cut off, the backlog limit and the duplicate check are all
     * applied here as well - nothing is trusted from the browser.
     */
    @PostMapping("/my-applications/apply")
    public String applyToDrive(@RequestParam Long driveId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        LoggedInUser user = currentUser(session);

        try {
            Application saved = applicationService.create(user.getStudentId(), driveId,
                    LocalDate.now(), "Applied by the student from the portal");

            redirectAttributes.addFlashAttribute("successMessage",
                    "Your application for " + saved.getDrive().getTitle()
                            + " has been submitted.");

        } catch (BusinessRuleException exception) {
            // closed drive, cut off not cleared, or already applied
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/my-profile";
    }

    private LoggedInUser currentUser(HttpSession session) {
        return (LoggedInUser) session.getAttribute("user");
    }
}
