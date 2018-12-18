package com.callcenter.test.integration.dao.repository;

import java.time.Instant;
import java.util.Optional;

import com.callcenter.dao.model.Employee;
import com.callcenter.dao.repository.EmployeeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(SpringRunner.class)
@DataMongoTest
@TestPropertySource("classpath:integration-test.properties")
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;


    @Test
    public void createEmployee() {
        Employee employee = generateEmployee(Employee.Role.RESPONDENT, Employee.Status.AVAILABLE);
        Employee savedEmployee = employeeRepository.save(employee);
        assertNotNull(savedEmployee);
        assertNotNull(savedEmployee.getEmployeeId());
    }

    @Test
    public void findAvailableEmployeeByStatusAndRole() {
        employeeRepository.save(generateEmployee(Employee.Role.RESPONDENT, Employee.Status.ON_CALL));
        employeeRepository.save(generateEmployee(Employee.Role.RESPONDENT, Employee.Status.AVAILABLE));

        Optional<Employee> firstByStatusAndRole = employeeRepository.findFirstByStatusAndRole(Employee.Status.AVAILABLE, Employee.Role.RESPONDENT);
        assertTrue(firstByStatusAndRole.isPresent());
    }

    @Test
    public void findAvailableEmployeeByStatusAndRoleNoneAvailable() {
        employeeRepository.save(generateEmployee(Employee.Role.RESPONDENT, Employee.Status.ON_CALL));
        employeeRepository.save(generateEmployee(Employee.Role.RESPONDENT, Employee.Status.ON_CALL));

        Optional<Employee> firstByStatusAndRole = employeeRepository.findFirstByStatusAndRole(Employee.Status.AVAILABLE, Employee.Role.RESPONDENT);
        assertFalse(firstByStatusAndRole.isPresent());
    }

    private Employee generateEmployee(Employee.Role role, Employee.Status status) {
        Employee employee = new Employee();
        employee.setFirstName("Test");
        employee.setLastName("User");
        employee.setCreated(Instant.now());
        employee.setRole(role);
        employee.setStatus(status);
        return employee;
    }
}
