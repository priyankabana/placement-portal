package com.college.placement.service;

import com.college.placement.exception.ResourceNotFoundException;
import com.college.placement.model.Department;
import com.college.placement.repository.DepartmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Department is a small master table, so this service only reads from it.
 * The lists it returns fill the branch dropdown on the student form.
 */
@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    // Constructor injection - Spring passes the repository in, nothing is created with "new"
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return departmentRepository.findAllByOrderByNameAsc();
    }

    public Department getById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No department found with id " + id));
    }
}
