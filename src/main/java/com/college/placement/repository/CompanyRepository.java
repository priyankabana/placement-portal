package com.college.placement.repository;

import com.college.placement.model.Company;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    // Method name itself becomes the where clause - no SQL needed here
    List<Company> findByNameContainingIgnoreCaseOrIndustryContainingIgnoreCaseOrderByNameAsc(
            String name, String industry);
}
