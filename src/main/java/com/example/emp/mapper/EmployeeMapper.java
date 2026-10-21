package com.example.emp.mapper;

import com.example.emp.dto.EmployeeRequest;
import com.example.emp.dto.EmployeeResponse;
import com.example.emp.entity.Department;
import com.example.emp.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee) {
        Department department = employee.getDepartment();
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getSalary(),
                department.getId(),
                department.getName());
    }

    public Employee toEntity(EmployeeRequest request, Department department) {
        Employee employee = new Employee();
        updateEntity(employee, request, department);
        return employee;
    }

    public void updateEntity(Employee employee, EmployeeRequest request, Department department) {
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setSalary(request.salary());
        employee.setDepartment(department);
    }
}
