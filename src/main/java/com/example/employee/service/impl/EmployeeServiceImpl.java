package com.example.employee.service.impl;

import com.example.employee.dto.*;
import com.example.employee.entity.*;
import com.example.employee.exception.*;
import com.example.employee.repository.*;
import com.example.employee.service.EmployeeService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {
    private static final Set<String> SORT_FIELDS = Set.of("id", "name", "email", "salary");
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;
    public EmployeeServiceImpl(EmployeeRepository employees, DepartmentRepository departments) {
        this.employees = employees; this.departments = departments;
    }
    @Override public EmployeeResponse create(EmployeeRequest request) {
        ensureEmailAvailable(request.email(), null);
        Department department = request.departmentId() == null ? null : findDepartment(request.departmentId());
        return toResponse(employees.save(new Employee(request.name().trim(), request.email().trim(), request.salary(), department)));
    }
    @Override @Transactional(readOnly = true) public Page<EmployeeResponse> getAll(int page, int size, String sortBy, String direction) {
        if (page < 0) throw new IllegalArgumentException("page must be zero or greater");
        if (size < 1 || size > 100) throw new IllegalArgumentException("size must be between 1 and 100");
        if (!SORT_FIELDS.contains(sortBy)) throw new IllegalArgumentException("sortBy must be one of: " + String.join(", ", SORT_FIELDS));
        Sort.Direction sortDirection;
        try { sortDirection = Sort.Direction.fromString(direction); }
        catch (IllegalArgumentException ex) { throw new IllegalArgumentException("direction must be 'asc' or 'desc'"); }
        return employees.findAll(PageRequest.of(page, size, Sort.by(sortDirection, sortBy))).map(this::toResponse);
    }
    @Override @Transactional(readOnly = true) public EmployeeResponse getById(Long id) { return toResponse(findEmployee(id)); }
    @Override public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployee(id);
        ensureEmailAvailable(request.email(), id);
        employee.setName(request.name().trim()); employee.setEmail(request.email().trim()); employee.setSalary(request.salary());
        setDepartment(employee, request.departmentId() == null ? null : findDepartment(request.departmentId()));
        return toResponse(employee);
    }
    @Override public void delete(Long id) { employees.delete(findEmployee(id)); }
    @Override @Transactional(readOnly = true) public List<EmployeeResponse> byDepartment(Long id) {
        if (!departments.existsById(id)) throw new DepartmentNotFoundException(id);
        return employees.findByDepartmentId(id).stream().map(this::toResponse).toList();
    }
    @Override @Transactional(readOnly = true) public List<EmployeeResponse> unassigned() {
        return employees.findByDepartmentIsNullOrderByIdAsc().stream().map(this::toResponse).toList();
    }
    @Override @Transactional(readOnly = true) public List<EmployeeResponse> salaryAbove(BigDecimal amount) {
        return employees.findBySalaryGreaterThan(amount).stream().map(this::toResponse).toList();
    }
    @Override @Transactional(readOnly = true) public List<EmployeeResponse> nameContains(String keyword) {
        return employees.searchByNameKeyword(keyword.trim()).stream().map(this::toResponse).toList();
    }
    @Override @Transactional(readOnly = true) public Page<EmployeeResponse> salaryBetween(BigDecimal min, BigDecimal max, int page, int size) {
        if (min.compareTo(max) > 0) throw new IllegalArgumentException("minimum salary must not exceed maximum salary");
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("page must be nonnegative and size must be between 1 and 100");
        return employees.findBySalaryRangeNative(min, max, PageRequest.of(page, size, Sort.by("salary"))).map(this::toResponse);
    }
    private Employee findEmployee(Long id) { return employees.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id)); }
    private Department findDepartment(Long id) { return departments.findById(id).orElseThrow(() -> new DepartmentNotFoundException(id)); }
    private void ensureEmailAvailable(String email, Long currentId) {
        employees.findByEmailIgnoreCase(email.trim()).filter(e -> !e.getId().equals(currentId))
                .ifPresent(e -> { throw new DuplicateResourceException("An employee with this email already exists"); });
    }
    private void setDepartment(Employee employee, Department next) {
        Department previous = employee.getDepartment();
        if (previous == next) return;
        if (previous != null) previous.removeEmployee(employee);
        if (next != null) next.addEmployee(employee); else employee.setDepartment(null);
    }
    private EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(e.getId(), e.getName(), e.getEmail(), e.getSalary(),
                e.getDepartment() == null ? null : e.getDepartment().getId(),
                e.getDepartment() == null ? null : e.getDepartment().getName());
    }
}
