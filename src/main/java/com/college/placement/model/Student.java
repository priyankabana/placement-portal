package com.college.placement.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * A final year student registered with the placement cell.
 *
 * Relationships:
 *   Student *---1 Department   (many students belong to one branch)
 *   Student 1---* Application  (one student can apply to many drives)
 */
@Entity
@Table(name = "student",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_student_roll", columnNames = "roll_number"),
                @UniqueConstraint(name = "uk_student_email", columnNames = "email")
        })
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Roll number is the college's own identifier, so it must stay unique
    @NotBlank(message = "Roll number is required")
    @Size(max = 15, message = "Roll number cannot cross 15 characters")
    @Column(name = "roll_number", nullable = false, length = 15)
    private String rollNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 40)
    @Column(name = "first_name", nullable = false, length = 40)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 40)
    @Column(name = "last_name", nullable = false, length = 40)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email id")
    @Column(nullable = false, length = 80)
    private String email;

    // Ten digits, nothing fancy - keeps the seed data and the form in sync
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    @Column(length = 10)
    private String phone;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot be more than 10")
    @Column(nullable = false)
    private Double cgpa;

    /** Passing out year, used to separate one batch from another. */
    @NotNull(message = "Batch year is required")
    @Min(value = 2015, message = "Batch year looks too old")
    @Max(value = 2035, message = "Batch year looks too far ahead")
    @Column(name = "batch_year", nullable = false)
    private Integer batchYear;

    @NotNull(message = "Backlog count is required")
    @Min(value = 0, message = "Backlogs cannot be negative")
    @Column(nullable = false)
    private Integer backlogs = 0;

    @Size(max = 150)
    @Column(name = "resume_url", length = 150)
    private String resumeUrl;

    /**
     * Login password of the student. It is not shown on the officer's form -
     * StudentService fills it with the default password when a student is
     * registered (roll number in small letters + @ + batch year) and stores
     * only its BCrypt hash.
     */
    @Size(max = 100)
    @Column(length = 100) // BCrypt hash, 60 characters
    private String password;

    // Foreign key column department_id in the student table
    @NotNull(message = "Please select a department")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "department_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_student_department"))
    private Department department;

    // Deleting a student also removes their application history
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    public Student() {
    }

    /** Convenience getter for the tables and the profile page heading. */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /** True once any one of the applications reaches the SELECTED stage. */
    public boolean isPlaced() {
        return applications.stream()
                .anyMatch(application -> application.getStatus() == ApplicationStatus.SELECTED);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getBatchYear() {
        return batchYear;
    }

    public void setBatchYear(Integer batchYear) {
        this.batchYear = batchYear;
    }

    public Integer getBacklogs() {
        return backlogs;
    }

    public void setBacklogs(Integer backlogs) {
        this.backlogs = backlogs;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }
}
