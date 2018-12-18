package com.callcenter.service;

import java.util.Optional;

import com.callcenter.controller.request.EmployeeRequest;
import com.callcenter.controller.response.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    /**
     * Create an employee.
     * @param employeeRequest
     * @return created employee
     */
    EmployeeResponse createEmployee(EmployeeRequest employeeRequest);

    /**
     * Get a list of employees by type
     * @param employeeType
     * @param pageRequest
     * @return page of employee by specified type
     */
    Page<EmployeeResponse> getEmployeesByType(String employeeType, Pageable pageRequest);

    /**
     * Get an employee whose status is set to AVAILABLE
     * @return optional of employee
     */
    Optional<EmployeeResponse> getAvailableEmployee();

    /**
     * update status of an employee by id.
     * Status can be one of the following: AVAILABLE, ON_CALL, UNAVAILABLE
     * @param employeeId
     * @param status
     */
    void updateStatusById(String employeeId, String status);
}
