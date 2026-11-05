package com.example.employee.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

// This request is validated before the service stores or updates an employee.
public record EmployeeRequest(@NotBlank @Size(max=120) String name,
                              @NotBlank @Email @Pattern(regexp="(?i)^[A-Za-z0-9._%+-]+@gmail\\.com$", message="Email must use the format example@gmail.com") @Size(max=254) String email,
                              @NotNull @Positive @Digits(integer=12, fraction=2) BigDecimal salary,
                              @Positive Long departmentId) {}
