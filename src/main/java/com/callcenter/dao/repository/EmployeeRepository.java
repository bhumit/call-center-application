package com.callcenter.dao.repository;

import java.util.Optional;

import com.callcenter.dao.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Mongo repository for storing employee info
 */
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    Page<Employee> findEmployeesByRole(String role, Pageable pageRequest);

    Optional<Employee> findFirstByStatusAndRole(Employee.Status status, Employee.Role role);
}
