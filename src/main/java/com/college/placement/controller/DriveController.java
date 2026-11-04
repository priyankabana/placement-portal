package com.college.placement.controller;

import com.college.placement.model.Company;
import com.college.placement.model.Drive;
import com.college.placement.model.DriveStatus;
import com.college.placement.service.CompanyService;
import com.college.placement.service.DriveService;
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
 * CRUD screens for campus drives, plus the quick status switch used from the list.
 */
@Controller
@RequestMapping("/drives")
public class DriveController {

    private final DriveService driveService;
    private final CompanyService companyService;

    public DriveController(DriveService driveService, CompanyService companyService) {
        this.driveService = driveService;
        this.companyService = companyService;
    }

    @ModelAttribute("companies")
    public List<Company> companies() {
        return companyService.findAll();
    }

    /** Both the form dropdown and the status buttons use these three values. */
    @ModelAttribute("statusOptions")
    public DriveStatus[] statusOptions() {
        return DriveStatus.values();
    }

    @ModelAttribute("activePage")
    public String activePage() {
        return "drives";
    }

    @GetMapping
    public String listDrives(Model model) {
        model.addAttribute("drives", driveService.findAll());
        return "drives/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("drive", new Drive());
        model.addAttribute("formTitle", "Announce new drive");
        return "drives/form";
    }

    @GetMapping("/{id}")
    public String showDrive(@PathVariable Long id, Model model) {
        Drive drive = driveService.getById(id);
        model.addAttribute("drive", drive);
        model.addAttribute("applications", driveService.findApplicationsOf(id));
        // list of students who clear the cut off and have not applied yet
        model.addAttribute("eligibleStudents", driveService.findEligibleStudents(drive));
        return "drives/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("drive", driveService.getById(id));
        model.addAttribute("formTitle", "Edit drive details");
        return "drives/form";
    }

    @PostMapping("/save")
    public String saveDrive(@Valid @ModelAttribute("drive") Drive drive,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        boolean isNew = drive.getId() == null;

        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", isNew ? "Announce new drive" : "Edit drive details");
            return "drives/form";
        }

        Drive saved = isNew
                ? driveService.create(drive)
                : driveService.update(drive.getId(), drive);

        redirectAttributes.addFlashAttribute("successMessage",
                saved.getTitle() + (isNew ? " drive was announced." : " drive was updated."));
        return "redirect:/drives/" + saved.getId();
    }

    /** Moves a drive between Upcoming / Open / Closed in one click. */
    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam DriveStatus status,
                               RedirectAttributes redirectAttributes) {
        Drive drive = driveService.changeStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage",
                drive.getTitle() + " is now marked as " + status.getLabel().toLowerCase() + ".");
        return "redirect:/drives";
    }

    @PostMapping("/{id}/delete")
    public String deleteDrive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Drive drive = driveService.getById(id);
        driveService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage",
                drive.getTitle() + " drive was deleted.");
        return "redirect:/drives";
    }
}
