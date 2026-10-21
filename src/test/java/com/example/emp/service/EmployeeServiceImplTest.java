package com.example.emp.service;

import com.example.emp.dto.EmployeePatchRequest;
import com.example.emp.dto.EmployeeRequest;
import com.example.emp.dto.EmployeeResponse;
import com.example.emp.entity.Department;
import com.example.emp.entity.Employee;
import com.example.emp.exception.DuplicateResourceException;
import com.example.emp.exception.ResourceNotFoundException;
import com.example.emp.mapper.EmployeeMapper;
import com.example.emp.repository.DepartmentRepository;
import com.example.emp.repository.EmployeeRepository;
import com.example.emp.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    private EmployeeServiceImpl employeeService;

    private Department engineering;
    private Employee alice;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, departmentRepository, new EmployeeMapper());

        engineering = new Department("Engineering");
        engineering.setId(1L);

        alice = new Employee();
        alice.setId(10L);
        alice.setName("Alice Tan");
        alice.setEmail("alice@example.com");
        alice.setSalary(new BigDecimal("8500.00"));
        alice.setDepartment(engineering);
    }

    @Test
    void getById_returnsEmployee_whenFound() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(alice));

        EmployeeResponse response = employeeService.getById(10L);

        assertThat(response.name()).isEqualTo("Alice Tan");
        assertThat(response.departmentName()).isEqualTo("Engineering");
    }

    @Test
    void getById_throwsNotFound_whenMissing() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesEmployee_whenEmailIsNew() {
        EmployeeRequest request = new EmployeeRequest("New Guy", "new@example.com", new BigDecimal("5000"), 1L);
        when(employeeRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(engineering));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee saved = invocation.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        EmployeeResponse response = employeeService.create(request);

        assertThat(response.id()).isEqualTo(20L);
        assertThat(response.email()).isEqualTo("new@example.com");
    }

    @Test
    void create_throwsConflict_whenEmailExists() {
        EmployeeRequest request = new EmployeeRequest("Copy", "alice@example.com", new BigDecimal("5000"), 1L);
        when(employeeRepository.existsByEmailIgnoreCase("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(DuplicateResourceException.class);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void patch_updatesOnlyProvidedFields() {
        when(employeeRepository.findById(10L)).thenReturn(Optional.of(alice));

        EmployeeResponse response = employeeService.patch(10L,
                new EmployeePatchRequest(null, null, new BigDecimal("9999.00"), null));

        assertThat(response.salary()).isEqualByComparingTo("9999.00");
        assertThat(response.name()).isEqualTo("Alice Tan");
        assertThat(response.email()).isEqualTo("alice@example.com");
    }

    @Test
    void delete_throwsNotFound_whenMissing() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
