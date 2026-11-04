package com.college.placement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Master table for branches (CSE, IT, ECE ...).
 *
 * The branch name used to be typed inside every student row, which repeated the
 * same text again and again. Moving it here keeps the design in 3NF - the student
 * table now only stores the department id.
 */
@Entity
@Table(name = "department",
        uniqueConstraints = @UniqueConstraint(name = "uk_department_code", columnNames = "code"))
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Department name is required")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String name;

    /** Short form printed in tables, e.g. CSE. */
    @NotBlank(message = "Department code is required")
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String code;

    @Size(max = 60)
    @Column(name = "hod_name", length = 60)
    private String hodName;

    public Department() {
        // JPA needs the no-argument constructor
    }

    public Department(String name, String code, String hodName) {
        this.name = name;
        this.code = code;
        this.hodName = hodName;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHodName() {
        return hodName;
    }

    public void setHodName(String hodName) {
        this.hodName = hodName;
    }
}
