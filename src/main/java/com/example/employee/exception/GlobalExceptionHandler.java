package com.example.employee.exception;

import com.example.employee.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({EmployeeNotFoundException.class, DepartmentNotFoundException.class})
    public ResponseEntity<ApiError> notFound(RuntimeException ex, HttpServletRequest req) { return error(HttpStatus.NOT_FOUND, ex.getMessage(), req, Map.of()); }
    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> badRequest(Exception ex, HttpServletRequest req) {
        String message = ex instanceof ConstraintViolationException ? "One or more request parameters are invalid" : "Invalid request: " + ex.getMessage();
        return error(HttpStatus.BAD_REQUEST, message, req, Map.of());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError e : ex.getBindingResult().getFieldErrors()) fields.putIfAbsent(e.getField(), e.getDefaultMessage());
        return error(HttpStatus.BAD_REQUEST, "Validation failed", req, fields);
    }
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> duplicate(DuplicateResourceException ex, HttpServletRequest req) { return error(HttpStatus.CONFLICT, ex.getMessage(), req, Map.of()); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> database(DataIntegrityViolationException ex, HttpServletRequest req) { return error(HttpStatus.CONFLICT, "The request conflicts with existing data", req, Map.of()); }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest req) { return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred", req, Map.of()); }
    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request, Map<String, String> fields) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), fields));
    }
}
