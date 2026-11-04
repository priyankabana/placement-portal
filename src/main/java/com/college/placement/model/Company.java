package com.college.placement.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * A recruiter that visits the campus.
 *
 * Company details (HR name, city, website) are stored only once here. The drive
 * table just points to the company id, so changing an HR contact does not mean
 * editing ten different drive rows.
 */
@Entity
@Table(name = "company",
        uniqueConstraints = @UniqueConstraint(name = "uk_company_name", columnNames = "name"))
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String name;

    @NotBlank(message = "Industry is required")
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String industry;

    @Size(max = 40)
    @Column(length = 40)
    private String city;

    @Size(max = 100)
    @Column(length = 100)
    private String website;

    @NotBlank(message = "HR contact person is required")
    @Size(max = 60)
    @Column(name = "hr_name", nullable = false, length = 60)
    private String hrName;

    @Email(message = "Enter a valid HR email id")
    @NotBlank(message = "HR email is required")
    @Column(name = "hr_email", nullable = false, length = 80)
    private String hrEmail;

    // A company can conduct several drives across batches
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Drive> drives = new ArrayList<>();

    public Company() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getHrName() {
        return hrName;
    }

    public void setHrName(String hrName) {
        this.hrName = hrName;
    }

    public String getHrEmail() {
        return hrEmail;
    }

    public void setHrEmail(String hrEmail) {
        this.hrEmail = hrEmail;
    }

    public List<Drive> getDrives() {
        return drives;
    }

    public void setDrives(List<Drive> drives) {
        this.drives = drives;
    }
}
