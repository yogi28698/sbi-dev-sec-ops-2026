package com.sbi.ems.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Join entity resolving the Many-to-Many relationship between {@link Employee}
 * and {@link Project}.
 *
 * Modelled as an explicit @Entity (not a plain @JoinTable) because it carries
 * its own payload: assignedDate and projectRole.
 *
 * Composite PK is represented by the embedded {@link EmployeeProjectId}.
 */
@Entity
@Table(name = "employee_project", indexes = { @Index(name = "idx_ep_employee", columnList = "employee_id"),
		@Index(name = "idx_ep_project", columnList = "project_id") })
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
@ToString(exclude = { "employee", "project" })
public class EmployeeProject {

	// -------------------------------------------------------------------------
	// Composite primary key (embedded)
	// -------------------------------------------------------------------------

	@EmbeddedId
	private EmployeeProjectId id;

	// -------------------------------------------------------------------------
	// Relationships (MapsId links FK columns to the embedded PK fields)
	// -------------------------------------------------------------------------

	/**
	 * Many EmployeeProject rows → One Employee. @MapsId("employeeId") maps this FK
	 * to the employeeId field in the embedded PK.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId("employeeId")
	@JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ep_employee"))
	private Employee employee;

	/**
	 * Many EmployeeProject rows → One Project. @MapsId("projectId") maps this FK to
	 * the projectId field in the embedded PK.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@MapsId("projectId")
	@JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_ep_project"))
	private Project project;

	// -------------------------------------------------------------------------
	// Payload columns
	// -------------------------------------------------------------------------

	@NotNull(message = "Assigned date is required")
	@PastOrPresent(message = "Assigned date cannot be in the future")
	@Column(name = "assigned_date", nullable = false)
	private LocalDate assignedDate;

	/**
	 * The role this employee plays on THIS project. Different from the employee's
	 * org-level Role entity. e.g. "Tech Lead", "Developer", "QA", "Business
	 * Analyst"
	 */
	@NotBlank(message = "Project role is required")
	@Size(min = 2, max = 100, message = "Project role must be between 2 and 100 characters")
	@Column(name = "project_role", nullable = false, length = 100)
	private String projectRole;

	// =========================================================================
	// Composite Primary Key — Embeddable
	// =========================================================================

	/**
	 * Embeddable composite key for EmployeeProject. Must implement Serializable as
	 * required by the JPA spec for @EmbeddedId.
	 */
	@Embeddable
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode // critical — JPA identity depends on correct equals/hashCode
	public static class EmployeeProjectId implements Serializable {

		@Serial
		private static final long serialVersionUID = 1L;

		@Column(name = "employee_id")
		private Long employeeId;

		@Column(name = "project_id")
		private Long projectId;
	}

	public EmployeeProject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EmployeeProject(EmployeeProjectId id, Employee employee, Project project,
			@NotNull(message = "Assigned date is required") @PastOrPresent(message = "Assigned date cannot be in the future") LocalDate assignedDate,
			@NotBlank(message = "Project role is required") @Size(min = 2, max = 100, message = "Project role must be between 2 and 100 characters") String projectRole) {
		super();
		this.id = id;
		this.employee = employee;
		this.project = project;
		this.assignedDate = assignedDate;
		this.projectRole = projectRole;
	}

	public EmployeeProjectId getId() {
		return id;
	}

	public void setId(EmployeeProjectId id) {
		this.id = id;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public LocalDate getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(LocalDate assignedDate) {
		this.assignedDate = assignedDate;
	}

	public String getProjectRole() {
		return projectRole;
	}

	public void setProjectRole(String projectRole) {
		this.projectRole = projectRole;
	}

}
