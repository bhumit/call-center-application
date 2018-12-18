package com.callcenter.test.unit.controller;

import java.util.Collections;

import com.callcenter.controller.EmployeeController;
import com.callcenter.controller.request.EmployeeRequest;
import com.callcenter.controller.response.EmployeeResponse;
import com.callcenter.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    private EmployeeController employeeController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        employeeController = new EmployeeController(employeeService);
    }

    @Test
    public void testCreateEmployee() {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        EmployeeRequest employeeRequest = new EmployeeRequest();
        Mockito.when(employeeService.createEmployee(employeeRequest)).thenReturn(employeeResponse);

        ResponseEntity<EmployeeResponse> employee = employeeController.createEmployee(employeeRequest);
        assertEquals(employeeResponse, employee.getBody());
        assertThat(employee.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void testGetEmployeesByType() {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        Mockito.when(employeeService.getEmployeesByType("RESPONDENT",PageRequest.of(0,1)))
            .thenReturn(new PageImpl<>(Collections.singletonList(employeeResponse)));

        ResponseEntity<Page<EmployeeResponse>> employees = employeeController.getEmployeesByType(EmployeeRequest.Role.RESPONDENT, PageRequest.of(0, 1));
        assertNotNull(employees);
        assertThat(employees.getStatusCode(), is(HttpStatus.OK));
        Page<EmployeeResponse> body = employees.getBody();
        assertNotNull(body);
        assertEquals(1,body.getTotalElements());
        EmployeeResponse response = body.getContent().get(0);
        assertThat(response, is(employeeResponse));
    }


}
