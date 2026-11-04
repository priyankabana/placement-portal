package com.college.placement.repository;

import com.college.placement.model.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByOrderByRollNumberAsc();

    Optional<Student> findByRollNumberIgnoreCase(String rollNumber);

    Optional<Student> findByEmailIgnoreCase(String email);

    // Used before insert/update so two students never share a roll number or mail id
    boolean existsByRollNumberIgnoreCase(String rollNumber);

    boolean existsByEmailIgnoreCase(String email);

    long countByDepartmentId(Long departmentId);

    List<Student> findByDepartmentIdOrderByRollNumberAsc(Long departmentId);

    /**
     * Search box on the student page. An empty keyword matches everything, and a
     * null departmentId means "all branches", so one query handles all four cases.
     */
    @Query("""
            select s from Student s join s.department d
            where (lower(s.firstName) like lower(concat('%', :keyword, '%'))
                   or lower(s.lastName) like lower(concat('%', :keyword, '%'))
                   or lower(s.rollNumber) like lower(concat('%', :keyword, '%')))
              and (:departmentId is null or d.id = :departmentId)
            order by s.rollNumber asc
            """)
    List<Student> search(@Param("keyword") String keyword,
                         @Param("departmentId") Long departmentId);

    /**
     * Students who clear the eligibility bar of a drive and have not applied to it yet.
     * The NOT IN sub query is what stops duplicate applications from being offered.
     */
    @Query("""
            select s from Student s
            where s.cgpa >= :minCgpa
              and s.backlogs <= :maxBacklogs
              and s.id not in (select a.student.id from Application a where a.drive.id = :driveId)
            order by s.cgpa desc, s.rollNumber asc
            """)
    List<Student> findEligibleStudents(@Param("minCgpa") Double minCgpa,
                                       @Param("maxBacklogs") Integer maxBacklogs,
                                       @Param("driveId") Long driveId);
}
