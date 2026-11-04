package com.college.placement.service;

import com.college.placement.exception.DuplicateRecordException;
import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.model.Company;
import com.college.placement.repository.CompanyRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/** CRUD for the recruiter master data. */
@Service
@Transactional(readOnly = true)
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> findAll() {
        return companyRepository.findAllByOrderByNameAsc();
    }

    /** One search box checks both the company name and the industry column. */
    public List<Company> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return findAll();
        }
        String trimmed = keyword.trim();
        return companyRepository
                .findByNameContainingIgnoreCaseOrIndustryContainingIgnoreCaseOrderByNameAsc(trimmed, trimmed);
    }

    public Company getById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No company found with id " + id));
    }

    public long count() {
        return companyRepository.count();
    }

    @Transactional
    public Company create(Company company) {
        if (companyRepository.existsByNameIgnoreCase(company.getName().trim())) {
            throw new DuplicateRecordException(
                    company.getName() + " is already added to the recruiter list.");
        }
        return companyRepository.save(company);
    }

    @Transactional
    public Company update(Long id, Company formData) {
        Company existing = getById(id);

        // renaming to a name that another recruiter already holds is not allowed
        boolean nameChanged = !existing.getName().equalsIgnoreCase(formData.getName().trim());
        if (nameChanged && companyRepository.existsByNameIgnoreCase(formData.getName().trim())) {
            throw new DuplicateRecordException(
                    formData.getName() + " is already added to the recruiter list.");
        }

        existing.setName(formData.getName());
        existing.setIndustry(formData.getIndustry());
        existing.setCity(formData.getCity());
        existing.setWebsite(formData.getWebsite());
        existing.setHrName(formData.getHrName());
        existing.setHrEmail(formData.getHrEmail());

        return companyRepository.save(existing);
    }

    @Transactional
    public void deleteById(Long id) {
        companyRepository.delete(getById(id));
    }
}
