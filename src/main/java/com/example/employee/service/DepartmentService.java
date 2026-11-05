package com.example.employee.service;

import com.example.employee.dto.DepartmentRequest;
import com.example.employee.dto.DepartmentResponse;
import com.example.employee.dto.EmployeeResponse;
import java.util.List;

public interface DepartmentService {
    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse update(Long id, DepartmentRequest request);
    void delete(Long id);
    List<DepartmentResponse> getAll();
    DepartmentResponse getById(Long id);
    List<EmployeeResponse> employees(Long id);
    DepartmentResponse assign(Long departmentId, Long employeeId);
}
