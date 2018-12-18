package com.callcenter.dao.model;

import java.time.Instant;

import com.callcenter.dao.DataModelConstants;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * DAO for employee
 */
@Data
@Document(collection = DataModelConstants.EMPLOYEE)
public class Employee {

    @Id
    private String employeeId;

    private String firstName;

    private String lastName;

    private Role role;

    private Status status;

    private Instant created;

    public enum Role {
        RESPONDENT, MANAGER, DIRECTOR
    }

    public enum Status {
        ON_CALL,
        AVAILABLE,
        UNAVAILABLE
    }
}
