package com.sbi.ems.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a company project that employees can be assigned to. A project can
 * have many employees; one employee can work on many projects. The many-to-many
 * is resolved via {@link EmployeeProject}.
 */
@Entity
@Table(name = "projects", uniqueConstraints = {
		@UniqueConstraint(name = "uk_project_name", columnNames = "name") }, indexes = {
				@Index(name = "idx_project_status", columnList = "status"),
				@Index(name = "idx_project_start_date", columnList = "start_date") })
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
@ToString(exclude = "employeeProjects") // avoid circular toString
public class Project {

	// -------------------------------------------------------------------------
	// Primary key
	// -------------------------------------------------------------------------

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	// -------------------------------------------------------------------------
	// Basic attributes
	// -------------------------------------------------------------------------

	@NotBlank(message = "Project name is required")
	@Size(min = 2, max = 150, message = "Project name must be between 2 and 150 characters")
	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Size(max = 500, message = "Description must not exceed 500 characters")
	@Column(name = "description", length = 500)
	private String description;

	// -------------------------------------------------------------------------
	// Timeline
	// -------------------------------------------------------------------------

	@NotNull(message = "Start date is required")
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	/**
	 * End date is optional — ongoing projects may not have a fixed end date.
	 * Business rule: endDate must be after startDate — validated at service layer.
	 */
	@Column(name = "end_date")
	private LocalDate endDate;

	// -------------------------------------------------------------------------
	// Status
	// -------------------------------------------------------------------------

	/**
	 * Project lifecycle status. PLANNED → ACTIVE → COMPLETED | ON_HOLD | CANCELLED
	 */
	@NotNull(message = "Project status is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private ProjectStatus status;

	// -------------------------------------------------------------------------
	// Audit timestamps
	// -------------------------------------------------------------------------

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// -------------------------------------------------------------------------
	// Relationships
	// -------------------------------------------------------------------------

	/**
	 * One project → Many EmployeeProject assignments. orphanRemoval = true: if an
	 * EmployeeProject is removed from this list, it is deleted from the DB
	 * automatically.
	 */
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE }, orphanRemoval = true)
	@Builder.Default
	private List<EmployeeProject> employeeProjects = new ArrayList<>();

	// -------------------------------------------------------------------------
	// Enum definition
	// -------------------------------------------------------------------------

	public enum ProjectStatus {
		PLANNED, ACTIVE, ON_HOLD, COMPLETED, CANCELLED
	}

	public Project() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Project(Long id,
			@NotBlank(message = "Project name is required") @Size(min = 2, max = 150, message = "Project name must be between 2 and 150 characters") String name,
			@Size(max = 500, message = "Description must not exceed 500 characters") String description,
			@NotNull(message = "Start date is required") LocalDate startDate, LocalDate endDate,
			@NotNull(message = "Project status is required") ProjectStatus status, LocalDateTime createdAt,
			LocalDateTime updatedAt, List<EmployeeProject> employeeProjects) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.employeeProjects = employeeProjects;
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

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public ProjectStatus getStatus() {
		return status;
	}

	public void setStatus(ProjectStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<EmployeeProject> getEmployeeProjects() {
		return employeeProjects;
	}

	public void setEmployeeProjects(List<EmployeeProject> employeeProjects) {
		this.employeeProjects = employeeProjects;
	}

}
