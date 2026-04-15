package com.sbi.ems.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a job role / designation within the organization.
 * e.g. SENIOR_ENGINEER, TEAM_LEAD, HR_MANAGER.
 * One role can be assigned to many employees.
 */
@Entity
@Table(
    name = "roles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_role_name", columnNames = "name")
    }
)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
@ToString(exclude = "employees")   // avoid circular toString with Employee
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // -------------------------------------------------------------------------
    // Basic attributes
    // -------------------------------------------------------------------------

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(name = "description", length = 255)
    private String description;

    /**
     * Seniority level — higher number means more senior.
     * e.g. 1 = Junior, 2 = Mid, 3 = Senior, 4 = Lead, 5 = Principal
     */
    @NotNull(message = "Role level is required")
    @Min(value = 1, message = "Level must be at least 1")
    @Max(value = 10, message = "Level must not exceed 10")
    @Column(name = "level", nullable = false)
    private Integer level;

    // -------------------------------------------------------------------------
    // Relationships
    // -------------------------------------------------------------------------

    /**
     * Bidirectional One-to-Many: one role assigned to many employees.
     * Owned by Employee side (mappedBy = "role").
     */
    @OneToMany(
        mappedBy = "role",
        fetch = FetchType.LAZY,
        cascade = { CascadeType.PERSIST, CascadeType.MERGE }
    )
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

	public Role() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Role(Long id,
			@NotBlank(message = "Role name is required") @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters") String name,
			@Size(max = 255, message = "Description must not exceed 255 characters") String description,
			@NotNull(message = "Role level is required") @Min(value = 1, message = "Level must be at least 1") @Max(value = 10, message = "Level must not exceed 10") Integer level,
			List<Employee> employees) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.level = level;
		this.employees = employees;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}
    
    
}
