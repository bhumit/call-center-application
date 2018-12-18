package com.callcenter.controller;

import javax.validation.Valid;

import com.callcenter.Constants;
import com.callcenter.controller.request.EmployeeRequest;
import com.callcenter.controller.response.EmployeeResponse;
import com.callcenter.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.CONTROLLER_API_CALL_CENTER_EMPLOYEE_CONTEXT)
public class EmployeeController {

    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;

    }

    /**
     * Entry point to create an employee
     * @param employeeRequest
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody @Valid EmployeeRequest employeeRequest) {
        EmployeeResponse employee = employeeService.createEmployee(employeeRequest);
        return ResponseEntity.ok(employee);
    }

    /**
     * entry point to get a page of employees by their role.
     * @param role
     * @param pageRequest
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getEmployeesByType(@RequestParam(defaultValue = "") EmployeeRequest.Role role, Pageable pageRequest) {
        Page<EmployeeResponse> employeesByType = employeeService.getEmployeesByType(role.name(), pageRequest);
        return ResponseEntity.ok(employeesByType);
    }
}
