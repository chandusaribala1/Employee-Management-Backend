package com.example.employee.service;

import com.example.employee.dto.*;
import org.springframework.data.domain.Page;
import java.math.BigDecimal;
import java.util.List;

public interface EmployeeService {
    EmployeeResponse create(EmployeeRequest request);
    Page<EmployeeResponse> getAll(int page, int size, String sortBy, String direction);
    EmployeeResponse getById(Long id);
    EmployeeResponse update(Long id, EmployeeRequest request);
    void delete(Long id);
    List<EmployeeResponse> byDepartment(Long departmentId);
    List<EmployeeResponse> unassigned();
    List<EmployeeResponse> salaryAbove(BigDecimal amount);
    List<EmployeeResponse> nameContains(String keyword);
    Page<EmployeeResponse> salaryBetween(BigDecimal minimum, BigDecimal maximum, int page, int size);
}
