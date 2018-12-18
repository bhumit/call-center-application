package com.callcenter.service;

import com.callcenter.controller.request.CallRequest;
import com.callcenter.controller.response.CallEndedResponse;
import com.callcenter.controller.response.CallResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CallHandlerService {

    /**
     * Queue a call for an employee. This method puts the call on RabbitMQ queue.
     * @param callRequest
     * @return
     */
    CallResponse queueCall(CallRequest callRequest);

    /**
     * Handle a call if an employee is available. This method checks if an employee is available,
     * if true -> reads the next message from RabbitMQ queue and handles the call
     * else do nothing
     */
    void handleNextQueuedCall();

    /**
     * Ends an ongoing call. This method also cancels call which is held in queue.
     * When the call handler process the call, it will simply drop the call.
     * @param callInfoId
     * @return
     */
    CallEndedResponse endCall(String callInfoId);

    /**
     * Get a page of queued calls
     * @param pageRequest
     * @return
     */
    Page<CallResponse> getQueuedCalls(Pageable pageRequest);
}
