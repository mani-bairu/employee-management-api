package com.example.emp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record EmployeeRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email String email,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal salary,
        @NotNull Long departmentId) {
}
