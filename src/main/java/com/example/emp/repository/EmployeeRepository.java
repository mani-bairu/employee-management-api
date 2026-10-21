package com.example.emp.repository;

import com.example.emp.dto.EmployeeDetailsResponse;
import com.example.emp.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = "department")
    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
            SELECT new com.example.emp.dto.EmployeeDetailsResponse(
                e.id, e.name, e.email, e.salary, d.id, d.name)
            FROM Employee e
            JOIN e.department d
            WHERE LOWER(d.name) = LOWER(:departmentName)
            ORDER BY e.name
            """)
    List<EmployeeDetailsResponse> findAllByDepartmentName(@Param("departmentName") String departmentName);
}
