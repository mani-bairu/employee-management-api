package com.example.emp.dto;

import java.math.BigDecimal;

public record EmployeeDetailsResponse(
        Long employeeId,
        String employeeName,
        String email,
        BigDecimal salary,
        Long departmentId,
        String departmentName) {
}
