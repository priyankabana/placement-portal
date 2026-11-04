package com.college.placement.repository;

import com.college.placement.model.Department;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data writes the implementation of these interfaces at runtime,
 * so we only declare what we need.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findAllByOrderByNameAsc();
}
