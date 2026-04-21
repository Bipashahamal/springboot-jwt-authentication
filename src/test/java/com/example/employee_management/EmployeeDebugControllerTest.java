package com.example.employee_management;

import com.example.employee_management.entity.Department;
import com.example.employee_management.entity.Employee;
import com.example.employee_management.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class EmployeeDebugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    @WithMockUser(authorities = "VIEW_EMPLOYEE")
    public void testGetEmployeeByIdSerialization() throws Exception {
        Department dept = Department.builder()
                .id(1L)
                .name("IT")
                .build();

        Employee emp = Employee.builder()
                .id(15L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .salary(50000.0)
                .department(dept)
                .build();

        when(employeeService.getEmployeeById(15L)).thenReturn(emp);

        mockMvc.perform(get("/api/employees/15"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
