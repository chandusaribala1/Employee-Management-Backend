package com.example.employee.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(Long id) { super("Department with ID " + id + " was not found"); }
}
