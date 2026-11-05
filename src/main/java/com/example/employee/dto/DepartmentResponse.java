package com.example.employee.dto;

import java.util.List;

public record DepartmentResponse(Long id, String name, List<EmployeeSummary> employees) {}
