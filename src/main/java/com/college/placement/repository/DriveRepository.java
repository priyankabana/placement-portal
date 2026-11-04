package com.college.placement.repository;

import com.college.placement.model.Drive;
import com.college.placement.model.DriveStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DriveRepository extends JpaRepository<Drive, Long> {

    List<Drive> findByStatusOrderByDriveDateAsc(DriveStatus status);

    List<Drive> findByCompanyIdOrderByDriveDateDesc(Long companyId);

    long countByStatus(DriveStatus status);

    /**
     * Drive list page. JOIN FETCH pulls the company in the same select instead of
     * firing one extra query per row (the classic N+1 problem).
     */
    @Query("select d from Drive d join fetch d.company c order by d.driveDate desc")
    List<Drive> findAllWithCompany();

    /** Dashboard widget - next few drives that have not happened yet. */
    @Query("""
            select d from Drive d join fetch d.company c
            where d.driveDate >= :fromDate and d.status <> :closedStatus
            order by d.driveDate asc
            """)
    List<Drive> findUpcoming(@Param("fromDate") LocalDate fromDate,
                             @Param("closedStatus") DriveStatus closedStatus);
}
