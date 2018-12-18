package com.callcenter.test.integration.service;

import java.util.Optional;

import com.callcenter.controller.request.EmployeeRequest;
import com.callcenter.controller.response.EmployeeResponse;
import com.callcenter.dao.model.Employee;
import com.callcenter.test.integration.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EmployeeServiceIntegrationTest extends BaseIntegrationTest {

    @Before
    public void cleanUp() {
        mongoOperations.dropCollection(Employee.class);
    }

    @Test
    public void getAvailableEmployeeReturnsRespondentIfRespondentAvailable() {
        EmployeeResponse respondent = createEmployee(EmployeeRequest.Role.RESPONDENT);
        createEmployee(EmployeeRequest.Role.MANAGER);
        createEmployee(EmployeeRequest.Role.DIRECTOR);

        EmployeeResponse nextAvailable = employeeService.getAvailableEmployee().get();
        assertThat(respondent, is(nextAvailable));
    }

    @Test
    public void getAvailableEmployeeReturnsManagerIfRespondentStatusIsNotAvailable() {
        createEmployee(EmployeeRequest.Role.RESPONDENT);
        makeACall();
        callHandlerService.handleNextQueuedCall();

        EmployeeResponse manager = createEmployee(EmployeeRequest.Role.MANAGER);
        createEmployee(EmployeeRequest.Role.DIRECTOR);

        EmployeeResponse nextAvailable = employeeService.getAvailableEmployee().get();
        assertThat(manager, is(nextAvailable));
    }

    @Test
    public void getAvailableEmployeeReturnsDirectorIfRespondentAndManagerStatusIsNotAvailable() {
        createEmployee(EmployeeRequest.Role.RESPONDENT);
        makeACall();
        callHandlerService.handleNextQueuedCall();

        createEmployee(EmployeeRequest.Role.MANAGER);
        makeACall();
        callHandlerService.handleNextQueuedCall();

        EmployeeResponse director = createEmployee(EmployeeRequest.Role.DIRECTOR);

        EmployeeResponse nextAvailable = employeeService.getAvailableEmployee().get();
        assertThat(director, is(nextAvailable));
    }

    @Test
    public void getAvailableEmployeeReturnsOptionalEmptyIfNoAvailableEmployees() {
        Optional<EmployeeResponse> employee = employeeService.getAvailableEmployee();
        assertThat(employee.isPresent(),is(false));


    }
}
