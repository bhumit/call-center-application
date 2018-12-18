package com.callcenter.controller.request;


import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * Request object for creating employee via the employee controller
 */
@Data
public class EmployeeRequest {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    private Role role;

    public enum Role {
        RESPONDENT("RESPONDENT"),
        MANAGER("MANAGER"),
        DIRECTOR("DIRECTOR");

        private String role;

        Role(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

}
