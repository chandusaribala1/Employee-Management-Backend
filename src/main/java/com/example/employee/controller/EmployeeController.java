package com.example.employee.controller;

import com.example.employee.dto.*;
import com.example.employee.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {
    private final EmployeeService service;
    public EmployeeController(EmployeeService service) { this.service = service; }
    @PostMapping public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }
    @GetMapping public PageResponse<EmployeeResponse> getAll(
            @RequestParam(defaultValue="0") @Min(0) int page,
            @RequestParam(defaultValue="10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue="id") String sortBy,
            @RequestParam(defaultValue="asc") String direction) {
        return pageResponse(service.getAll(page, size, sortBy, direction), sortBy, direction);
    }
    @GetMapping("/unassigned") public List<EmployeeResponse> unassigned() { return service.unassigned(); }
    @GetMapping("/{id}") public EmployeeResponse getById(@PathVariable @Positive Long id) { return service.getById(id); }
    @PutMapping("/{id}") public EmployeeResponse update(@PathVariable @Positive Long id, @Valid @RequestBody EmployeeRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable @Positive Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/department/{departmentId}") public List<EmployeeResponse> byDepartment(@PathVariable @Positive Long departmentId) { return service.byDepartment(departmentId); }
    @GetMapping("/search/salary") public List<EmployeeResponse> salaryAbove(@RequestParam @NotNull @Positive BigDecimal amount) { return service.salaryAbove(amount); }
    @GetMapping("/search/name") public List<EmployeeResponse> nameContains(@RequestParam @NotBlank String keyword) { return service.nameContains(keyword); }
    @GetMapping("/search/salary-range") public PageResponse<EmployeeResponse> salaryBetween(
            @RequestParam @NotNull @Positive BigDecimal minimum, @RequestParam @NotNull @Positive BigDecimal maximum,
            @RequestParam(defaultValue="0") @Min(0) int page, @RequestParam(defaultValue="10") @Min(1) @Max(100) int size) {
        return pageResponse(service.salaryBetween(minimum, maximum, page, size), "salary", "asc");
    }
    private PageResponse<EmployeeResponse> pageResponse(Page<EmployeeResponse> p, String sortBy, String direction) {
        return new PageResponse<>(p.getContent(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast(), sortBy, direction);
    }
}
