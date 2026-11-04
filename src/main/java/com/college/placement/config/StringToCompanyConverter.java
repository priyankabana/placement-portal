package com.college.placement.config;

import com.college.placement.model.Company;
import com.college.placement.service.CompanyService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Same idea as the department converter, used by the company dropdown of a drive. */
@Component
public class StringToCompanyConverter implements Converter<String, Company> {

    private final CompanyService companyService;

    public StringToCompanyConverter(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public Company convert(String id) {
        if (!StringUtils.hasText(id)) {
            return null;
        }
        return companyService.getById(Long.valueOf(id));
    }
}
