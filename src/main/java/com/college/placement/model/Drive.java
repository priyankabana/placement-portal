package com.college.placement.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * One recruitment drive announced by a company - the job opening students apply to.
 *
 * The eligibility rule of the college is kept as two columns (minCgpa, maxBacklogs)
 * so the shortlisting query can be written straight in JPQL.
 */
@Entity
@Table(name = "drive")
public class Drive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Please select the company")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "company_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_drive_company"))
    private Company company;

    @NotBlank(message = "Job role is required")
    @Size(max = 70)
    @Column(name = "job_role", nullable = false, length = 70)
    private String jobRole;

    /** Annual package in lakhs per annum, the number students actually ask about. */
    @NotNull(message = "Package is required")
    @DecimalMin(value = "0.5", message = "Package looks too low")
    @DecimalMax(value = "200.0", message = "Package looks too high")
    @Column(name = "package_lpa", nullable = false)
    private Double packageLpa;

    @NotNull(message = "Minimum CGPA is required")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0", message = "Minimum CGPA cannot be more than 10")
    @Column(name = "min_cgpa", nullable = false)
    private Double minCgpa;

    @NotNull(message = "Allowed backlog count is required")
    @Min(value = 0)
    @Column(name = "max_backlogs", nullable = false)
    private Integer maxBacklogs = 0;

    @NotNull(message = "Drive date is required")
    @Column(name = "drive_date", nullable = false)
    private LocalDate driveDate;

    @Size(max = 60)
    @Column(length = 60)
    private String location;

    @Size(max = 500, message = "Please keep the description under 500 characters")
    @Column(length = 500)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private DriveStatus status = DriveStatus.UPCOMING;

    @OneToMany(mappedBy = "drive", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public Drive() {
    }

    public String getDriveDateDisplay() {
        return driveDate == null ? "-" : driveDate.format(DISPLAY_DATE);
    }

    /** Handy for the drive card heading: "TCS - System Engineer". */
    public String getTitle() {
        return (company != null ? company.getName() : "Company") + " - " + jobRole;
    }

    /** Applications are accepted only while the drive is open. */
    public boolean isAcceptingApplications() {
        return status == DriveStatus.OPEN;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public Double getPackageLpa() {
        return packageLpa;
    }

    public void setPackageLpa(Double packageLpa) {
        this.packageLpa = packageLpa;
    }

    public Double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(Double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public Integer getMaxBacklogs() {
        return maxBacklogs;
    }

    public void setMaxBacklogs(Integer maxBacklogs) {
        this.maxBacklogs = maxBacklogs;
    }

    public LocalDate getDriveDate() {
        return driveDate;
    }

    public void setDriveDate(LocalDate driveDate) {
        this.driveDate = driveDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DriveStatus getStatus() {
        return status;
    }

    public void setStatus(DriveStatus status) {
        this.status = status;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
