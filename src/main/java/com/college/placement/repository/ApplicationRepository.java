package com.college.placement.repository;

import com.college.placement.model.Application;
import com.college.placement.model.ApplicationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Guard for the unique (student, drive) pair
    boolean existsByStudentIdAndDriveId(Long studentId, Long driveId);

    List<Application> findByStudentIdOrderByAppliedOnDesc(Long studentId);

    List<Application> findByDriveIdOrderByStatusAscIdAsc(Long driveId);

    List<Application> findByStatusOrderByAppliedOnDesc(ApplicationStatus status);

    long countByStatus(ApplicationStatus status);

    /** Latest activity feed on the dashboard. */
    List<Application> findTop6ByOrderByAppliedOnDescIdDesc();

    /**
     * Three table join - application, student and drive - listing every confirmed
     * offer with the best package first.
     */
    @Query("""
            select a from Application a
            join fetch a.student s
            join fetch a.drive d
            join fetch d.company c
            where a.status = :status
            order by d.packageLpa desc
            """)
    List<Application> findOffers(@Param("status") ApplicationStatus status);

    /**
     * DISTINCT matters here: a student selected by two companies must still be
     * counted as one placed student.
     */
    @Query("select count(distinct a.student.id) from Application a where a.status = :status")
    long countPlacedStudents(@Param("status") ApplicationStatus status);

    @Query("""
            select count(distinct a.student.id) from Application a
            where a.status = :status and a.student.department.id = :departmentId
            """)
    long countPlacedStudentsByDepartment(@Param("status") ApplicationStatus status,
                                         @Param("departmentId") Long departmentId);

    /** Highest package accepted so far, shown as a dashboard tile. */
    @Query("select max(a.drive.packageLpa) from Application a where a.status = :status")
    Double findHighestPackage(@Param("status") ApplicationStatus status);

    @Query("select avg(a.drive.packageLpa) from Application a where a.status = :status")
    Double findAveragePackage(@Param("status") ApplicationStatus status);
}
