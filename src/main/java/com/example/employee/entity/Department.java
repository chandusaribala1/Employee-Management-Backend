package com.example.employee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments", schema = "employee", uniqueConstraints = @UniqueConstraint(name = "uk_department_name", columnNames = "name"))
public class Department {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank @Size(max = 100) @Column(nullable = false, length = 100)
    private String name;
    @OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST)
    private List<Employee> employees = new ArrayList<>();

    protected Department() {}
    public Department(String name) { this.name = name; }
    public void addEmployee(Employee employee) {
        if (!employees.contains(employee)) employees.add(employee);
        employee.setDepartment(this);
    }
    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        if (employee.getDepartment() == this) employee.setDepartment(null);
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Employee> getEmployees() { return employees; }
}
