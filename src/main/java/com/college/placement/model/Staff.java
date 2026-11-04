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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Login account of the college side users - the placement officer and the
 * department faculty.
 *
 * Staff details are kept in their own table instead of hard coding a user id
 * and password in the code, so a new faculty account is just one more row.
 */
@Entity
@Table(name = "staff",
        uniqueConstraints = @UniqueConstraint(name = "uk_staff_username", columnNames = "username"))
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String name;

    /** What the user types in the login form. */
    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100) // BCrypt hash, 60 characters
    private String password;

    @Size(max = 60)
    @Column(length = 60)
    private String designation;

    @Email
    @Size(max = 80)
    @Column(length = 80)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserRole role = UserRole.STAFF;

    /** Null for the placement officer, filled for a department faculty. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id",
            foreignKey = @ForeignKey(name = "fk_staff_department"))
    private Department department;

    public Staff() {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
