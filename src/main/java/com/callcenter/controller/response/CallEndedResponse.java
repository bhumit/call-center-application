package com.callcenter.controller.response;

import lombok.Data;

/**
 * Response object when a user ends the call
 */
@Data
public class CallEndedResponse {

    private String duration;
    private String callerId;
    private String callerName;
}
