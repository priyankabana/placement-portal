package com.college.placement.repository;

import com.college.placement.model.Staff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    // used by the login form
    Optional<Staff> findByUsernameIgnoreCase(String username);

    List<Staff> findAllByOrderByNameAsc();
}
