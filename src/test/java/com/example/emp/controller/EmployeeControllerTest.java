package com.example.emp.controller;

import com.example.emp.dto.EmployeeRequest;
import com.example.emp.dto.EmployeeResponse;
import com.example.emp.exception.ResourceNotFoundException;
import com.example.emp.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    private static final String BASE = "/api/v1/employees";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    private final EmployeeResponse alice = new EmployeeResponse(
            1L, "Alice Tan", "alice@example.com", new BigDecimal("8500.00"), 1L, "Engineering");

    @Test
    void get_returns200() throws Exception {
        when(employeeService.getById(1L)).thenReturn(alice);

        mockMvc.perform(get(BASE + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice Tan"))
                .andExpect(jsonPath("$.departmentName").value("Engineering"));
    }

    @Test
    void get_returns404_whenMissing() throws Exception {
        when(employeeService.getById(99L)).thenThrow(new ResourceNotFoundException("Employee", 99L));

        mockMvc.perform(get(BASE + "/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void post_returns201() throws Exception {
        when(employeeService.create(any(EmployeeRequest.class))).thenReturn(alice);

        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Alice Tan","email":"alice@example.com","salary":8500.00,"departmentId":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void post_returns400_whenBodyInvalid() throws Exception {
        mockMvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"not-an-email","salary":-5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.departmentId").exists());
    }

    @Test
    void put_returns200() throws Exception {
        when(employeeService.update(eq(1L), any(EmployeeRequest.class))).thenReturn(alice);

        mockMvc.perform(put(BASE + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Alice Tan","email":"alice@example.com","salary":8500.00,"departmentId":1}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void patch_returns200() throws Exception {
        when(employeeService.patch(eq(1L), any())).thenReturn(alice);

        mockMvc.perform(patch(BASE + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"salary":9000.00}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(employeeService).delete(1L);

        mockMvc.perform(delete(BASE + "/1"))
                .andExpect(status().isNoContent());
    }
}
