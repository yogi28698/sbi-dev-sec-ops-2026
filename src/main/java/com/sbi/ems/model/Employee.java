package com.sbi.ems.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Central entity representing an employee in the organization.
 *
 * Relationships: - Many employees → One department (owning side) - Many
 * employees → One role (owning side) - Many employees ↔ Many projects (via
 * EmployeeProject join entity)
 */
@Entity
@Table(name = "employees", uniqueConstraints = {
		@UniqueConstraint(name = "uk_employee_email", columnNames = "email") }, indexes = {
				@Index(name = "idx_employee_department", columnList = "department_id"),
				@Index(name = "idx_employee_role", columnList = "role_id"),
				@Index(name = "idx_employee_status", columnList = "status") })
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Builder
@ToString(exclude = { "department", "role", "employeeProjects" })
public class Employee {

	// -------------------------------------------------------------------------
	// Primary key
	// -------------------------------------------------------------------------

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	// -------------------------------------------------------------------------
	// Personal information
	// -------------------------------------------------------------------------

	@NotBlank(message = "First name is required")
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be a valid email address")
	@Size(max = 100, message = "Email must not exceed 100 characters")
	@Column(name = "email", nullable = false, length = 100, unique = true)
	private String email;

	@Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be 10-15 digits, optionally starting with +")
	@Column(name = "phone", length = 20)
	private String phone;

	// -------------------------------------------------------------------------
	// Employment details
	// -------------------------------------------------------------------------

	/**
	 * Salary stored as BigDecimal for precision — never use float/double for money.
	 * Scale 2 = two decimal places (e.g. 75000.00). PII field — mask in logs via
	 * AOP.
	 */
	@NotNull(message = "Salary is required")
	@DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
	@Digits(integer = 10, fraction = 2, message = "Salary must have at most 10 integer digits and 2 decimal places")
	@Column(name = "salary", nullable = false, precision = 12, scale = 2)
	private BigDecimal salary;

	@NotNull(message = "Hire date is required")
	@PastOrPresent(message = "Hire date cannot be in the future")
	@Column(name = "hire_date", nullable = false)
	private LocalDate hireDate;

	/**
	 * Employment status — stored as a string enum in DB for readability. ACTIVE |
	 * INACTIVE | ON_LEAVE | TERMINATED
	 */
	@NotNull(message = "Status is required")
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private EmployeeStatus status;

	// -------------------------------------------------------------------------
	// Audit timestamps (auto-managed by Hibernate)
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
	 * Many employees → One department. Owning side — holds the FK column
	 * "department_id". LAZY fetch — don't load department unless explicitly
	 * accessed.
	 */
	@NotNull(message = "Department is required")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "department_id", nullable = false, foreignKey = @ForeignKey(name = "fk_employee_department"))
	private Department department;

	/**
	 * Many employees → One role. Owning side — holds the FK column "role_id".
	 */
	@NotNull(message = "Role is required")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_employee_role"))
	private Role role;

	/**
	 * One employee → Many EmployeeProject assignments. Cascade PERSIST + MERGE only
	 * — do not delete projects when employee is removed.
	 */
	@OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE }, orphanRemoval = true)
	@Builder.Default
	private List<EmployeeProject> employeeProjects = new ArrayList<>();

	// -------------------------------------------------------------------------
	// Enum definition
	// -------------------------------------------------------------------------

	public enum EmployeeStatus {
		ACTIVE, INACTIVE, ON_LEAVE, TERMINATED
	}

	public Employee() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Employee(Long id,
			@NotBlank(message = "First name is required") @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName,
			@NotBlank(message = "Last name is required") @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName,
			@NotBlank(message = "Email is required") @Email(message = "Email must be a valid email address") @Size(max = 100, message = "Email must not exceed 100 characters") String email,
			@Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be 10-15 digits, optionally starting with +") String phone,
			@NotNull(message = "Salary is required") @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0") @Digits(integer = 10, fraction = 2, message = "Salary must have at most 10 integer digits and 2 decimal places") BigDecimal salary,
			@NotNull(message = "Hire date is required") @PastOrPresent(message = "Hire date cannot be in the future") LocalDate hireDate,
			@NotNull(message = "Status is required") EmployeeStatus status, LocalDateTime createdAt,
			LocalDateTime updatedAt, @NotNull(message = "Department is required") Department department,
			@NotNull(message = "Role is required") Role role, List<EmployeeProject> employeeProjects) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.salary = salary;
		this.hireDate = hireDate;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.department = department;
		this.role = role;
		this.employeeProjects = employeeProjects;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public LocalDate getHireDate() {
		return hireDate;
	}

	public void setHireDate(LocalDate hireDate) {
		this.hireDate = hireDate;
	}

	public EmployeeStatus getStatus() {
		return status;
	}

	public void setStatus(EmployeeStatus status) {
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

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public List<EmployeeProject> getEmployeeProjects() {
		return employeeProjects;
	}

	public void setEmployeeProjects(List<EmployeeProject> employeeProjects) {
		this.employeeProjects = employeeProjects;
	}
	
	
}
