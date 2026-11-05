package com.example.employee.dto;

import java.util.List;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements,
                              int totalPages, boolean last, String sortBy, String direction) {}
