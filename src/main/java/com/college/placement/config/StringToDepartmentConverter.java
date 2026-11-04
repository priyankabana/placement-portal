package com.college.placement.config;

import com.college.placement.model.Department;
import com.college.placement.service.DepartmentService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * The branch dropdown posts only the department id ("3"). Spring MVC needs to
 * turn that text into a real Department object before it can be set on the
 * Student form object - this converter does exactly that.
 *
 * Being a @Component is enough, Spring Boot registers it with the MVC
 * conversion service on its own.
 */
@Component
public class StringToDepartmentConverter implements Converter<String, Department> {

    private final DepartmentService departmentService;

    public StringToDepartmentConverter(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Override
    public Department convert(String id) {
        // blank means the user left the dropdown untouched - @NotNull then reports it
        if (!StringUtils.hasText(id)) {
            return null;
        }
        return departmentService.getById(Long.valueOf(id));
    }
}
