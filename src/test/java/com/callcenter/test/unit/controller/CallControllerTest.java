package com.callcenter.test.unit.controller;

import java.util.UUID;

import com.callcenter.controller.CallController;
import com.callcenter.controller.request.CallRequest;
import com.callcenter.controller.response.CallEndedResponse;
import com.callcenter.controller.response.CallResponse;
import com.callcenter.service.CallHandlerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

public class CallControllerTest {

    @Mock
    private CallHandlerService callHandlerService;

    private CallController callController;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        callController = new CallController(callHandlerService);

    }

    @Test
    public void testHandleCall() {
        CallRequest callRequest = new CallRequest();
        CallResponse callResponse = new CallResponse();
        callResponse.setId(UUID.randomUUID().toString());
        Mockito.when(callHandlerService.queueCall(callRequest)).thenReturn(callResponse);

        ResponseEntity<CallResponse> responseEntity = callController.handleCall(callRequest);

        assertNotNull(responseEntity);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        CallResponse actualResponse = responseEntity.getBody();
        assertThat(actualResponse, is(callResponse));
    }

    @Test
    public void testEndCall() {
        String callInfoId = UUID.randomUUID().toString();
        CallEndedResponse callEndedResponse = new CallEndedResponse();
        Mockito.when(callHandlerService.endCall(callInfoId)).thenReturn(callEndedResponse);
        ResponseEntity<CallEndedResponse> responseEntity = callController.endCall(callInfoId);

        assertNotNull(responseEntity);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

        CallEndedResponse actualResponse = responseEntity.getBody();

        assertThat(actualResponse, is(callEndedResponse));
    }
}
