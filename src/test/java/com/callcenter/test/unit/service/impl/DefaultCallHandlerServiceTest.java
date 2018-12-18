package com.callcenter.test.unit.service.impl;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.callcenter.controller.request.CallRequest;
import com.callcenter.controller.response.CallEndedResponse;
import com.callcenter.controller.response.CallResponse;
import com.callcenter.dao.model.CallInfo;
import com.callcenter.dao.repository.CallInfoRepository;
import com.callcenter.exception.CallCenterServiceException;
import com.callcenter.service.EmployeeService;
import com.callcenter.service.impl.DefaultCallHandlerService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;

public class DefaultCallHandlerServiceTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private CallInfoRepository callInfoRepository;

    private DefaultCallHandlerService callHandlerService;

    @Mock
    private RabbitTemplate rabbitTemplate;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        callHandlerService = new DefaultCallHandlerService(employeeService,callInfoRepository, rabbitTemplate);
    }

    @Test
    public void testQueueCall() {
        CallRequest callRequest = new CallRequest();
        callRequest.setCallerId("+4407961");
        callRequest.setCallerName("Test User");

        CallInfo callInfo = new CallInfo();
        callInfo.setId(UUID.randomUUID().toString());
        Mockito.when(callInfoRepository.save(any(CallInfo.class))).thenReturn(callInfo);

        CallResponse callResponse = callHandlerService.queueCall(callRequest);

        assertNotNull(callResponse);
        assertEquals(callInfo.getId(), callResponse.getId());
    }

    @Test
    public void testEndCall() {
        String callInfoId = UUID.randomUUID().toString();
        CallInfo callInfo = new CallInfo();
        callInfo.setStartedAt(Instant.now());
        callInfo.setCallerId("+441234");
        callInfo.setCallerName("Test User");

        Mockito.when(callInfoRepository.findById(callInfoId)).thenReturn(Optional.of(callInfo));

        CallEndedResponse callEndedResponse = callHandlerService.endCall(callInfoId);
        assertNotNull(callEndedResponse);

        assertThat(callEndedResponse.getCallerId(), is(callInfo.getCallerId()));
        assertThat(callEndedResponse.getCallerName(), is(callInfo.getCallerName()));
        assertThat(callEndedResponse.getDuration(), is(notNullValue()));

    }

    @Test(expected = CallCenterServiceException.class)
    public void testEndCallInvalidCallInfoId() {
        String id = "not-valid-id";
        Mockito.when(callInfoRepository.findById(id)).thenReturn(Optional.empty());
        callHandlerService.endCall(id);
    }

    @Test
    public void testGetQueuedCalls() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        CallInfo callInfo = new CallInfo();
        callInfo.setId(UUID.randomUUID().toString());
        Mockito.when(callInfoRepository.findCallInfoByisQueuedTrueOrderByReceivedAt(pageRequest)).thenReturn(new PageImpl<>(Collections.singletonList(callInfo)));
        Page<CallResponse> calls = callHandlerService.getQueuedCalls(pageRequest);
        List<CallResponse> callResponses = calls.getContent();
        assertNotNull(callResponses);
        assertThat(callResponses.size(), is(1));

        CallResponse callResponse = callResponses.get(0);
        assertNotNull(callResponse);
        assertThat(callResponse.getId(), is(callInfo.getId()));


    }
}
