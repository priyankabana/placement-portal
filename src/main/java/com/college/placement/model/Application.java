package com.college.placement.model;

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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Bridge (junction) table between Student and Drive.
 *
 * Student and Drive share a many-to-many relation in real life, and this entity
 * resolves it into two many-to-one relations. It also carries its own data -
 * the applied date, the current stage and the interviewer remark - which is
 * exactly why a plain @ManyToMany was not used here.
 */
@Entity
@Table(name = "application",
        uniqueConstraints = @UniqueConstraint(name = "uk_application_student_drive",
                columnNames = {"student_id", "drive_id"}))
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Please select a student")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_application_student"))
    private Student student;

    @NotNull(message = "Please select a drive")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "drive_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_application_drive"))
    private Drive drive;

    @NotNull(message = "Applied date is required")
    @Column(name = "applied_on", nullable = false)
    private LocalDate appliedOn = LocalDate.now();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Size(max = 200, message = "Remark should be within 200 characters")
    @Column(length = 200)
    private String remarks;

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public Application() {
    }

    /** Date printed on the pages, e.g. 28 Jul 2026. */
    public String getAppliedOnDisplay() {
        return appliedOn == null ? "-" : appliedOn.format(DISPLAY_DATE);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Drive getDrive() {
        return drive;
    }

    public void setDrive(Drive drive) {
        this.drive = drive;
    }

    public LocalDate getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(LocalDate appliedOn) {
        this.appliedOn = appliedOn;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
