package com.example.emp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EmployeePatchRequest(
        @Size(min = 1, max = 100) String name,
        @Email String email,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal salary,
        Long departmentId) {
}
