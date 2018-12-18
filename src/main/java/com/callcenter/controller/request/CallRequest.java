package com.callcenter.controller.request;

import lombok.Data;

/**
 * Request object for incoming call request via controller
 */
@Data
public class CallRequest {

    private String callerId;
    private String callerName;
}
