package com.example.employee_management;

import com.example.employee_management.controller.EmployeeController;
import com.example.employee_management.entity.Department;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.service.EmployeeService;
import com.example.employee_management.security.CustomUserDetailsService;
import com.example.employee_management.security.RateLimitingFilter;
import com.example.employee_management.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false) // Bypass security filters for debugging
@Import(EmployeeDebugControllerTest.RateLimitingFilterTestConfig.class)
public class EmployeeDebugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private FileService fileService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private RateLimitingFilter rateLimitingFilter;

    @Configuration
    static class RateLimitingFilterTestConfig {
        @Bean
        public RateLimitingFilter rateLimitingFilter() {
            return org.mockito.Mockito.mock(RateLimitingFilter.class);
        }
    }

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser
    public void testGetEmployeeByIdSerialization() throws Exception {
        // Setup mock data
        Department dept = Department.builder()
                .id(1L)
                .name("IT")
                .description("IT Department")
                .build();

        Employee emp = Employee.builder()
                .id(15L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department(dept)
                .salary(50000.0)
                .build();

        when(employeeService.getEmployeeById(15L)).thenReturn(emp);

        // Perform request and print output (this will show the stack trace if
        // serialization fails)
        mockMvc.perform(get("/api/employees/15"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
