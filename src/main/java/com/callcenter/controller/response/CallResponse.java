package com.callcenter.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * Response object when a user places a call via the controller
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallResponse {

    private String id;

}
