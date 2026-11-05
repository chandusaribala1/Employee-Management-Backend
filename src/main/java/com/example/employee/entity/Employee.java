package com.example.employee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "employees", schema = "employee", uniqueConstraints = @UniqueConstraint(name = "uk_employee_email", columnNames = "email"))
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 120) @Column(nullable = false, length = 120)
    private String name;
    @NotBlank @Email @Size(max = 254) @Column(nullable = false, length = 254)
    private String email;
    @NotNull @Positive @Digits(integer = 12, fraction = 2) @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal salary;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", foreignKey = @ForeignKey(name = "fk_employee_department"))
    private Department department;

    protected Employee() {}
    public Employee(String name, String email, BigDecimal salary, Department department) {
        this.name = name; this.email = email; this.salary = salary;
        if (department != null) department.addEmployee(this);
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}
