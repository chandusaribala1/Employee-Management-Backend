package com.example.employee.controller;

import com.example.employee.dto.*;
import com.example.employee.service.DepartmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentService service;
    public DepartmentController(DepartmentService service) { this.service = service; }
    @PostMapping public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request)); }
    @PutMapping("/{id}") public DepartmentResponse update(@PathVariable @Positive Long id, @Valid @RequestBody DepartmentRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
    @GetMapping public List<DepartmentResponse> getAll() { return service.getAll(); }
    @GetMapping("/{id}") public DepartmentResponse getById(@PathVariable @Positive Long id) { return service.getById(id); }
    @GetMapping("/{id}/employees") public List<EmployeeResponse> employees(@PathVariable @Positive Long id) { return service.employees(id); }
    @PostMapping("/{departmentId}/employees/{employeeId}")
    public DepartmentResponse assign(@PathVariable @Positive Long departmentId, @PathVariable @Positive Long employeeId) { return service.assign(departmentId, employeeId); }
}
