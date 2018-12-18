package com.callcenter.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.callcenter.controller.request.CallRequest;
import com.callcenter.dao.model.CallInfo;
import com.callcenter.dao.repository.CallInfoRepository;
import com.callcenter.exception.CallCenterServiceException;
import com.callcenter.service.CallHandlerService;
import com.callcenter.service.EmployeeService;
import com.callcenter.controller.response.CallEndedResponse;
import com.callcenter.controller.response.CallResponse;
import com.callcenter.controller.response.EmployeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultCallHandlerService implements CallHandlerService {

    private RabbitTemplate rabbitTemplate;
    private EmployeeService employeeService;
    private CallInfoRepository callInfoRepository;

    @Value("${callcenter.queue.name}")
    private String queueName;

    @Value("${callcenter.topic-exchange.name}")
    private String routingKey;

    @Autowired
    public DefaultCallHandlerService(EmployeeService employeeService, CallInfoRepository callInfoRepository, RabbitTemplate rabbitTemplate) {
        this.employeeService = employeeService;
        this.callInfoRepository = callInfoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public CallResponse queueCall(CallRequest callRequest){
        // create call info object in the db
        CallInfo callInfo = new CallInfo();
        callInfo.setCallerId(callRequest.getCallerId());
        callInfo.setCallerName(callRequest.getCallerName());
        callInfo.setReceivedAt(Instant.now());
        callInfo.setQueued(true);
        CallInfo savedCallInfo = callInfoRepository.save(callInfo);

        rabbitTemplate.convertAndSend(routingKey,savedCallInfo.getId());
        log.info(String.format("Queuing call for %s (%s) ID = %s", callRequest.getCallerName(), callRequest.getCallerId(), savedCallInfo.getId()));

        return toCallResponse(savedCallInfo);
    }

    @Scheduled(cron = "${scheduling.handlecall.cron}")
    @Override
    public void handleNextQueuedCall() {
        Optional<EmployeeResponse> employeeResponse = employeeService.getAvailableEmployee();
        employeeResponse.ifPresent(employee -> {
            String queuedCallId = rabbitTemplate.receiveAndConvert(queueName, ParameterizedTypeReference.forType(String.class));
            if(StringUtils.isBlank(queuedCallId)) return;
            log.info(String.format("received id %s from queue..", queuedCallId));

            Optional<CallInfo> optionalQueuedCall = callInfoRepository.findById(queuedCallId);
            optionalQueuedCall.ifPresent(callInfo -> {
                if(callInfo.getEndedAt() != null) return;
                employeeService.updateStatusById(employee.getId(), "ON_CALL");
                log.info(String.format("queued call from %s (%s) ID = %s is being handled by %s %s", callInfo.getCallerName(), callInfo.getCallerId(), callInfo.getId(), employee.getFirstName(), employee.getLastName()));
                callInfo.setInProgress(true);
                callInfo.setEmployeeId(employee.getId());
                callInfo.setStartedAt(Instant.now());
                callInfo.setQueued(false);
                callInfoRepository.save(callInfo);
            });
        });
    }

    @Override
    public CallEndedResponse endCall(String callInfoId) {
        CallInfo callInfo = callInfoRepository.findById(callInfoId).orElseThrow(() -> new CallCenterServiceException("callInfoId does not exist", HttpStatus.NOT_FOUND));

        boolean wasInQueue = callInfo.isQueued();

        callInfo.setEndedAt(Instant.now());
        callInfo.setInProgress(false);
        callInfo.setQueued(false);

        // update call info
        callInfoRepository.save(callInfo);

        // free up the call handler if call to end was not in queue
        if(!wasInQueue) {
            employeeService.updateStatusById(callInfo.getEmployeeId(), "AVAILABLE");
        }

        return toCallEndedResponse(callInfo);
    }

    @Override
    public Page<CallResponse> getQueuedCalls(Pageable pageRequest) {
        Page<CallInfo> calls = callInfoRepository.findCallInfoByisQueuedTrueOrderByReceivedAt(pageRequest);
        List<CallResponse> callInfos = calls.stream()
            .map(DefaultCallHandlerService::toCallResponse).collect(Collectors.toList());

        return new PageImpl<>(callInfos,calls.getPageable(), calls.getTotalElements());
    }

    private static CallResponse toCallResponse(CallInfo callInfo) {
        CallResponse callResponse = new CallResponse();
        callResponse.setId(callInfo.getId());
        return callResponse;
    }

    private CallEndedResponse toCallEndedResponse(CallInfo callInfo) {
        CallEndedResponse callEndedResponse = new CallEndedResponse();
        callEndedResponse.setCallerId(callInfo.getCallerId());
        callEndedResponse.setCallerName(callInfo.getCallerName());

        if(callInfo.getStartedAt() != null && callInfo.getEndedAt() != null) {
            callEndedResponse.setDuration(String.format("%d minutes", Duration.between(callInfo.getStartedAt(), callInfo.getEndedAt()).toMinutes()));
        }
        return callEndedResponse;
    }
}
