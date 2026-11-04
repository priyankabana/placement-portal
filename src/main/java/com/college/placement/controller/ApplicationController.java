package com.college.placement.controller;

import com.college.placement.dto.ApplicationForm;
import com.college.placement.exception.BusinessRuleException;
import com.college.placement.model.Application;
import com.college.placement.model.ApplicationStatus;
import com.college.placement.service.ApplicationService;
import com.college.placement.service.DriveService;
import com.college.placement.service.StudentService;
import jakarta.validation.Valid;
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
 * Screens for the application module - apply on behalf of a student, then move
 * that application through the interview stages.
 */
@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final StudentService studentService;
    private final DriveService driveService;

    public ApplicationController(ApplicationService applicationService,
                                 StudentService studentService,
                                 DriveService driveService) {
        this.applicationService = applicationService;
        this.studentService = studentService;
        this.driveService = driveService;
    }

    @ModelAttribute("statusOptions")
    public ApplicationStatus[] statusOptions() {
        return ApplicationStatus.values();
    }

    @ModelAttribute("activePage")
    public String activePage() {
        return "applications";
    }

    /** An optional status parameter turns the list into a filtered list. */
    @GetMapping
    public String listApplications(@RequestParam(required = false) ApplicationStatus status,
                                   Model model) {
        model.addAttribute("applications",
                status == null ? applicationService.findAll() : applicationService.findByStatus(status));
        model.addAttribute("selectedStatus", status);
        return "applications/list";
    }

    /**
     * The form can be opened plain, or with ?studentId= / ?driveId= already
     * filled - that is how the "Apply" buttons on the drive page work.
     */
    @GetMapping("/new")
    public String showApplyForm(@RequestParam(required = false) Long studentId,
                                @RequestParam(required = false) Long driveId,
                                Model model) {
        ApplicationForm form = new ApplicationForm();
        form.setStudentId(studentId);
        form.setDriveId(driveId);

        model.addAttribute("applicationForm", form);
        addFormDropdowns(model);
        return "applications/form";
    }

    @PostMapping("/save")
    public String saveApplication(@Valid @ModelAttribute("applicationForm") ApplicationForm form,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormDropdowns(model);
            return "applications/form";
        }

        try {
            Application saved = applicationService.create(
                    form.getStudentId(), form.getDriveId(), form.getAppliedOn(), form.getRemarks());

            redirectAttributes.addFlashAttribute("successMessage",
                    saved.getStudent().getFullName() + " applied to " + saved.getDrive().getTitle() + ".");
            return "redirect:/applications";

        } catch (BusinessRuleException exception) {
            // duplicate application, closed drive or eligibility failure
            model.addAttribute("errorMessage", exception.getMessage());
            addFormDropdowns(model);
            return "applications/form";
        }
    }

    /** Status update posted from the small inline form in the list. */
    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ApplicationStatus status,
                               @RequestParam(required = false) String remarks,
                               RedirectAttributes redirectAttributes) {

        Application updated = applicationService.updateStatus(id, status, remarks);
        redirectAttributes.addFlashAttribute("successMessage",
                updated.getStudent().getFullName() + " is now marked as "
                        + status.getLabel().toLowerCase() + ".");
        return "redirect:/applications";
    }

    @PostMapping("/{id}/delete")
    public String deleteApplication(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        applicationService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Application was withdrawn.");
        return "redirect:/applications";
    }

    // the two dropdowns are needed whenever the form is rendered, success or failure
    private void addFormDropdowns(Model model) {
        model.addAttribute("students", studentService.findAll());
        model.addAttribute("openDrives", driveService.findOpenDrives());
    }
}
