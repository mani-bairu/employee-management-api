package com.example.emp.service.impl;

import com.example.emp.dto.EmployeeDetailsResponse;
import com.example.emp.dto.EmployeePatchRequest;
import com.example.emp.dto.EmployeeRequest;
import com.example.emp.dto.EmployeeResponse;
import com.example.emp.dto.PageResponse;
import com.example.emp.entity.Department;
import com.example.emp.entity.Employee;
import com.example.emp.exception.DuplicateResourceException;
import com.example.emp.exception.ResourceNotFoundException;
import com.example.emp.mapper.EmployeeMapper;
import com.example.emp.repository.DepartmentRepository;
import com.example.emp.repository.EmployeeRepository;
import com.example.emp.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(findEmployee(id));
    }

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        ensureEmailIsFree(request.email());
        Department department = findDepartment(request.departmentId());
        Employee saved = employeeRepository.save(employeeMapper.toEntity(request, department));
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployee(id);
        if (!employee.getEmail().equalsIgnoreCase(request.email())) {
            ensureEmailIsFree(request.email());
        }
        employeeMapper.updateEntity(employee, request, findDepartment(request.departmentId()));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse patch(Long id, EmployeePatchRequest request) {
        Employee employee = findEmployee(id);
        if (request.name() != null) {
            employee.setName(request.name());
        }
        if (request.email() != null && !request.email().equalsIgnoreCase(employee.getEmail())) {
            ensureEmailIsFree(request.email());
            employee.setEmail(request.email());
        }
        if (request.salary() != null) {
            employee.setSalary(request.salary());
        }
        if (request.departmentId() != null) {
            employee.setDepartment(findDepartment(request.departmentId()));
        }
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        employeeRepository.delete(findEmployee(id));
    }

    @Override
    public PageResponse<EmployeeResponse> search(String name, Pageable pageable) {
        String keyword = name == null ? "" : name.trim();
        return PageResponse.from(employeeRepository
                .findByNameContainingIgnoreCase(keyword, pageable)
                .map(employeeMapper::toResponse));
    }

    @Override
    public List<EmployeeDetailsResponse> getByDepartmentName(String departmentName) {
        departmentRepository.findByNameIgnoreCase(departmentName)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentName));
        return employeeRepository.findAllByDepartmentName(departmentName);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));
    }

    private void ensureEmailIsFree(String email) {
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Employee with email " + email + " already exists");
        }
    }
}
