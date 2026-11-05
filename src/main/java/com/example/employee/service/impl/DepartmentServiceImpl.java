package com.example.employee.service.impl;

import com.example.employee.dto.*;
import com.example.employee.entity.*;
import com.example.employee.exception.*;
import com.example.employee.repository.*;
import com.example.employee.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departments;
    private final EmployeeRepository employees;
    public DepartmentServiceImpl(DepartmentRepository departments, EmployeeRepository employees) { this.departments = departments; this.employees = employees; }
    @Override public DepartmentResponse create(DepartmentRequest request) {
        if (departments.existsByNameIgnoreCase(request.name().trim())) throw new DuplicateResourceException("A department with this name already exists");
        return toResponse(departments.save(new Department(request.name().trim())));
    }
    @Override public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findDepartment(id);
        String name = request.name().trim();
        if (departments.existsByNameIgnoreCaseAndIdNot(name, id)) throw new DuplicateResourceException("A department with this name already exists");
        department.setName(name);
        return toResponse(departments.save(department));
    }
    @Override public void delete(Long id) {
        Department department = findDepartment(id);
        for (Employee employee : employees.findByDepartmentId(id)) {
            department.removeEmployee(employee);
            employees.save(employee);
        }
        departments.delete(department);
    }
    @Override @Transactional(readOnly = true) public List<DepartmentResponse> getAll() { return departments.findAll().stream().map(this::toResponse).toList(); }
    @Override @Transactional(readOnly = true) public DepartmentResponse getById(Long id) { return toResponse(findDepartment(id)); }
    @Override @Transactional(readOnly = true) public List<EmployeeResponse> employees(Long id) {
        if (!departments.existsById(id)) throw new DepartmentNotFoundException(id);
        return employees.findByDepartmentId(id).stream().map(e -> new EmployeeResponse(e.getId(), e.getName(), e.getEmail(), e.getSalary(), id,
                e.getDepartment().getName())).toList();
    }
    @Override public DepartmentResponse assign(Long departmentId, Long employeeId) {
        Department department = findDepartment(departmentId);
        Employee employee = employees.findById(employeeId).orElseThrow(() -> new EmployeeNotFoundException(employeeId));
        if (employee.getDepartment() != null && employee.getDepartment() != department) employee.getDepartment().removeEmployee(employee);
        department.addEmployee(employee);
        employees.save(employee);
        return toResponse(department);
    }
    private Department findDepartment(Long id) { return departments.findById(id).orElseThrow(() -> new DepartmentNotFoundException(id)); }
    private DepartmentResponse toResponse(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getEmployees().stream()
                .map(e -> new EmployeeSummary(e.getId(), e.getName(), e.getEmail())).toList());
    }
}
