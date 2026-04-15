package com.sbi.ems.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an organizational department within the company. One department
 * can have many employees.
 */
@Entity
@Table(name = "departments", uniqueConstraints = {
		@UniqueConstraint(name = "uk_department_name", columnNames = "name") })
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
@ToString(exclude = "employees") // avoid circular toString with Employee
public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	// -------------------------------------------------------------------------
	// Basic attributes
	// -------------------------------------------------------------------------

	@NotBlank(message = "Department name is required")
	@Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Size(max = 255, message = "Description must not exceed 255 characters")
	@Column(name = "description", length = 255)
	private String description;

	// -------------------------------------------------------------------------
	// Relationships
	// -------------------------------------------------------------------------

	/**
	 * Bidirectional One-to-Many: one department has many employees. Owned by
	 * Employee side (mappedBy = "department"). CascadeType.ALL excluded
	 * intentionally — deleting a department should not cascade-delete employees;
	 * handle via business logic.
	 */
	@OneToMany(mappedBy = "department", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@Builder.Default
	private List<Employee> employees = new ArrayList<>();

	public Department(Long id,
			@NotBlank(message = "Department name is required") @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters") String name,
			@Size(max = 255, message = "Description must not exceed 255 characters") String description,
			List<Employee> employees) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.employees = employees;
	}

	public Department() {
		super();
		// TODO Auto-generated constructor stub
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

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

}
