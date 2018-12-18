package com.callcenter.test.unit.service.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import com.callcenter.controller.request.EmployeeRequest;
import com.callcenter.controller.response.EmployeeResponse;
import com.callcenter.dao.model.Employee;
import com.callcenter.dao.repository.EmployeeRepository;
import com.callcenter.service.impl.DefaultEmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class DefaultEmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private DefaultEmployeeService employeeService;

    @Captor
    private ArgumentCaptor<Employee> employeeArgumentCaptor;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        employeeService = new DefaultEmployeeService(employeeRepository);
    }

    @Test
    public void testCreateEmployee() {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName("Jane");
        employeeRequest.setLastName("Doe");
        employeeRequest.setRole(EmployeeRequest.Role.DIRECTOR);


        Employee employee = new Employee();
        employee.setRole(Employee.Role.DIRECTOR);
        employee.setEmployeeId(UUID.randomUUID().toString());
        Mockito.when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse employeeResponse = employeeService.createEmployee(employeeRequest);
        assertNotNull(employeeResponse.getId());

        // capture the outgoing request to employee repository
        Mockito.verify(employeeRepository).save(employeeArgumentCaptor.capture());
        Employee employeeArgumentCaptorValue = employeeArgumentCaptor.getValue();

        assertNotNull(employeeArgumentCaptorValue);
        assertThat(employeeRequest.getFirstName(), is(employeeArgumentCaptorValue.getFirstName()));
        assertThat(employeeRequest.getLastName(), is(employeeArgumentCaptorValue.getLastName()));
        assertThat(employeeRequest.getRole().name(), is(employeeArgumentCaptorValue.getRole().name()));
        assertThat(employeeArgumentCaptorValue.getStatus(),is(Employee.Status.AVAILABLE));

    }

    @Test
    public void testGetAvailableEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeId(UUID.randomUUID().toString());
        employee.setFirstName("Test");
        employee.setLastName("User");
        employee.setRole(Employee.Role.RESPONDENT);
        employee.setStatus(Employee.Status.AVAILABLE);

        Mockito.when(employeeRepository.findEmployeesByRole(Employee.Role.RESPONDENT.name(), PageRequest.of(0,1)))
            .thenReturn(new PageImpl<>(Collections.singletonList(employee)));

        Optional<EmployeeResponse> availableEmployee = employeeService.getAvailableEmployee();
        assertNotNull(availableEmployee);
    }

    @Test
    public void testGetAvailableEmployeeWhenNonAvailable() {

        Mockito.when(employeeRepository.findEmployeesByRole(Employee.Role.RESPONDENT.name(), PageRequest.of(0,1)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));

        Optional<EmployeeResponse> availableEmployee = employeeService.getAvailableEmployee();
        assertFalse(availableEmployee.isPresent());
    }

    @Test
    public void testUpdateStatusById() {
        String employeeId = UUID.randomUUID().toString();
        Mockito.when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(new Employee()));
        employeeService.updateStatusById(employeeId, Employee.Status.ON_CALL.name());

        Mockito.verify(employeeRepository).save(employeeArgumentCaptor.capture());
        Employee employeeArgumentCaptorValue = employeeArgumentCaptor.getValue();
        Employee.Status status = employeeArgumentCaptorValue.getStatus();
        assertThat(status, is(Employee.Status.ON_CALL));

    }
}
