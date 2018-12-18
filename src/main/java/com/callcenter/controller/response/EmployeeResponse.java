package com.callcenter.controller.response;

import lombok.Data;


/**
 * Response object when a user requests a list of employee
 */
@Data
public class EmployeeResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String role;
    private String status;
}
