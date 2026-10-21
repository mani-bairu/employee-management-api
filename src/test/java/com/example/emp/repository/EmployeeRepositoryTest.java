package com.example.emp.repository;

import com.example.emp.dto.EmployeeDetailsResponse;
import com.example.emp.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void findAllByDepartmentName_returnsEmployeesOfThatDepartment() {
        List<EmployeeDetailsResponse> result = employeeRepository.findAllByDepartmentName("engineering");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.departmentName().equals("Engineering"));
        assertThat(result.get(0).employeeName()).isEqualTo("Alice Tan");
    }

    @Test
    void findByName_returnsRequestedPage() {
        Page<Employee> page = employeeRepository.findByNameContainingIgnoreCase("",
                PageRequest.of(0, 2, Sort.by("name")));

        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent()).extracting(Employee::getName).containsExactly("Alice Tan", "Bob Lee");
    }

    @Test
    void findByName_isCaseInsensitive() {
        Page<Employee> page = employeeRepository.findByNameContainingIgnoreCase("ALICE", PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(Employee::getEmail).containsExactly("alice@example.com");
    }
}
