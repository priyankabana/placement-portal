package com.college.placement.controller;

import com.college.placement.exception.BusinessRuleException;
import com.college.placement.model.Company;
import com.college.placement.service.CompanyService;
import com.college.placement.service.DriveService;
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

/** CRUD screens for the recruiters that visit the campus. */
@Controller
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final DriveService driveService;

    public CompanyController(CompanyService companyService, DriveService driveService) {
        this.companyService = companyService;
        this.driveService = driveService;
    }

    @ModelAttribute("activePage")
    public String activePage() {
        return "companies";
    }

    @GetMapping
    public String listCompanies(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("companies", companyService.search(keyword));
        model.addAttribute("keyword", keyword);
        return "companies/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("company", new Company());
        model.addAttribute("formTitle", "Add new recruiter");
        return "companies/form";
    }

    @GetMapping("/{id}")
    public String showCompany(@PathVariable Long id, Model model) {
        model.addAttribute("company", companyService.getById(id));
        // drives of this company are listed on the same page
        model.addAttribute("drives", driveService.findByCompany(id));
        return "companies/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("company", companyService.getById(id));
        model.addAttribute("formTitle", "Edit recruiter details");
        return "companies/form";
    }

    @PostMapping("/save")
    public String saveCompany(@Valid @ModelAttribute("company") Company company,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        boolean isNew = company.getId() == null;

        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", isNew ? "Add new recruiter" : "Edit recruiter details");
            return "companies/form";
        }

        try {
            Company saved = isNew
                    ? companyService.create(company)
                    : companyService.update(company.getId(), company);

            redirectAttributes.addFlashAttribute("successMessage",
                    saved.getName() + (isNew ? " was added." : " was updated."));
            return "redirect:/companies/" + saved.getId();

        } catch (BusinessRuleException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("formTitle", isNew ? "Add new recruiter" : "Edit recruiter details");
            return "companies/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCompany(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Company company = companyService.getById(id);
        companyService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage",
                company.getName() + " and its drives were removed.");
        return "redirect:/companies";
    }
}
