package com.callcenter.controller;

import javax.validation.Valid;

import com.callcenter.Constants;
import com.callcenter.controller.request.CallRequest;
import com.callcenter.controller.response.CallEndedResponse;
import com.callcenter.controller.response.CallResponse;
import com.callcenter.service.CallHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.CONTROLLER_API_CALL_CENTER_HANDLER_CONTEXT)
public class CallController {


    private CallHandlerService callHandlerService;

    /**
     * @param callHandlerService
     */
    @Autowired
    public CallController(CallHandlerService callHandlerService) {
        this.callHandlerService = callHandlerService;
    }

    /**
     * entry point for incoming call request
     * @param callRequest
     * @return
     */
    @PostMapping("/dispatchCall")
    public ResponseEntity<CallResponse> handleCall(@Valid @RequestBody CallRequest callRequest) {
        CallResponse response = callHandlerService.queueCall(callRequest);
        return ResponseEntity.ok(response);

    }

    /**
     * entry point for ending call
     * @param callInfoId
     * @return
     */
    @GetMapping("/endcall/{callInfoId}")
    public ResponseEntity<CallEndedResponse> endCall(@PathVariable String callInfoId) {
        return ResponseEntity.ok(callHandlerService.endCall(callInfoId));
    }

    /**
     * entry point for getting currently queued calls
     * @param pageRequest
     * @return
     */
    @GetMapping("/queuedCalls")
    public ResponseEntity<Page<CallResponse>> getQueuedCall(Pageable pageRequest) {
        Page<CallResponse> queuedCalls = callHandlerService.getQueuedCalls(pageRequest);
        return ResponseEntity.ok(queuedCalls);
    }
}
