package com.callcenter.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.callcenter.dao.model.Employee;
import com.callcenter.dao.repository.EmployeeRepository;
import com.callcenter.exception.CallCenterServiceException;
import com.callcenter.controller.request.EmployeeRequest;
import com.callcenter.controller.response.EmployeeResponse;
import com.callcenter.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultEmployeeService implements EmployeeService {

    private EmployeeRepository employeeRepository;

    @Autowired
    public DefaultEmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest employeeRequest) {
        // employee to persist
        Employee employee = new Employee();
        employee.setCreated(Instant.now());
        employee.setFirstName(employeeRequest.getFirstName());
        employee.setLastName(employeeRequest.getLastName());
        employee.setRole(Employee.Role.valueOf(employeeRequest.getRole().name()));
        employee.setStatus(Employee.Status.AVAILABLE);

        // save employee
        Employee savedEmployee = employeeRepository.save(employee);

        // employee to return back
        return toEmployeeResponse(savedEmployee);
    }

    @Override
    public Page<EmployeeResponse> getEmployeesByType(String employeeType,Pageable pageRequest) {
        Page<Employee> employeesByRole = employeeRepository.findEmployeesByRole(employeeType, pageRequest);
        List<EmployeeResponse> employeeResponseList = employeesByRole
            .stream()
            .map(DefaultEmployeeService::toEmployeeResponse)
            .collect(Collectors.toList());
        return new PageImpl<>(employeeResponseList,employeesByRole.getPageable(), employeesByRole.getTotalElements());
    }

    @Override
    public Optional<EmployeeResponse> getAvailableEmployee() {
        Optional<Employee> employeeOptional = Stream.of(getAvailableRespondent(), getAvailableManager(), getAvailableDirector())
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

        if(employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            EmployeeResponse employeeResponse = toEmployeeResponse(employee);
            return Optional.of(employeeResponse);
        }

        return Optional.empty();

    }

    @Override
    public void updateStatusById(String employeeId, String status)  {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new CallCenterServiceException("EmployeeId does not exist", HttpStatus.NOT_FOUND));
        employee.setStatus(Employee.Status.valueOf(status));
        employeeRepository.save(employee);
    }

    private Optional<Employee> getAvailableRespondent() {
        return employeeRepository.findFirstByStatusAndRole(Employee.Status.AVAILABLE, Employee.Role.RESPONDENT);
    }

    private Optional<Employee> getAvailableManager() {
        return employeeRepository.findFirstByStatusAndRole(Employee.Status.AVAILABLE, Employee.Role.MANAGER);
    }

    private Optional<Employee> getAvailableDirector() {
        return employeeRepository.findFirstByStatusAndRole(Employee.Status.AVAILABLE, Employee.Role.DIRECTOR);
    }

    private static EmployeeResponse toEmployeeResponse(Employee employee) {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setFirstName(employee.getFirstName());
        employeeResponse.setLastName(employee.getLastName());
        employeeResponse.setId(employee.getEmployeeId());
        employeeResponse.setRole(employee.getRole().name());

        if(employee.getStatus() != null) {
            employeeResponse.setStatus(employee.getStatus().name());
        }

        return employeeResponse;
    }
}
