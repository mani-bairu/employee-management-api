package com.example.emp.service;

import com.example.emp.dto.EmployeeDetailsResponse;
import com.example.emp.dto.EmployeePatchRequest;
import com.example.emp.dto.EmployeeRequest;
import com.example.emp.dto.EmployeeResponse;
import com.example.emp.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse getById(Long id);

    EmployeeResponse create(EmployeeRequest request);

    EmployeeResponse update(Long id, EmployeeRequest request);

    EmployeeResponse patch(Long id, EmployeePatchRequest request);

    void delete(Long id);

    PageResponse<EmployeeResponse> search(String name, Pageable pageable);

    List<EmployeeDetailsResponse> getByDepartmentName(String departmentName);
}
