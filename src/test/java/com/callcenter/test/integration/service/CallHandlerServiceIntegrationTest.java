package com.callcenter.test.integration.service;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.callcenter.controller.request.EmployeeRequest;
import com.callcenter.controller.response.CallResponse;
import com.callcenter.controller.response.EmployeeResponse;
import com.callcenter.dao.model.CallInfo;
import com.callcenter.dao.model.Employee;
import com.callcenter.test.integration.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class CallHandlerServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Value("${callcenter.queue.name}")
    private String queueName;

    @Before
    public void cleanUp() {
        rabbitAdmin.purgeQueue(queueName, true);
        mongoOperations.dropCollection(CallInfo.class);
        mongoOperations.dropCollection(Employee.class);
    }


    @Test
    public void respondentHandlesQueuedCallsWhenAvailable() {
        // GIVEN:
        EmployeeResponse employeeRespondent = createEmployee(EmployeeRequest.Role.RESPONDENT);
        EmployeeResponse employeeManager = createEmployee(EmployeeRequest.Role.MANAGER);
        EmployeeResponse employeeDirector = createEmployee(EmployeeRequest.Role.DIRECTOR);

        // WHEN:
        makeACall();
        // calling handleNextQueuedCall method manually as this method is ran on schedule of every 5 seconds
        callHandlerService.handleNextQueuedCall();

        // THEN:
        Employee respondent = employeeRepository.findById(employeeRespondent.getId()).get();
        assertThat(respondent.getStatus(),is(Employee.Status.ON_CALL));

        Employee manager = employeeRepository.findById(employeeManager.getId()).get();
        assertThat(manager.getStatus(),is(Employee.Status.AVAILABLE));

        Employee director = employeeRepository.findById(employeeDirector.getId()).get();
        assertThat(director.getStatus(),is(Employee.Status.AVAILABLE));
    }

    @Test
    public void managerHandlesQueuedCallsWhenNoRespondentIsAvailable() {
        // setting respondent to attend a call
        createEmployee(EmployeeRequest.Role.RESPONDENT);
        makeACall();
        callHandlerService.handleNextQueuedCall();


        // GIVEN:
        EmployeeResponse employeeManager = createEmployee(EmployeeRequest.Role.MANAGER);
        EmployeeResponse employeeDirector = createEmployee(EmployeeRequest.Role.DIRECTOR);

        // WHEN:
        makeACall();
        callHandlerService.handleNextQueuedCall();


        // THEN:
        Employee manager = employeeRepository.findById(employeeManager.getId()).get();
        assertThat(manager.getStatus(),is(Employee.Status.ON_CALL));

        Employee director = employeeRepository.findById(employeeDirector.getId()).get();
        assertThat(director.getStatus(),is(Employee.Status.AVAILABLE));
    }

    @Test
    public void directorHandlesQueuedCallsWhenNoRespondentsOrNoManagersAreAvailable() {
        // setting respondent and manager to attend a call
        createEmployee(EmployeeRequest.Role.RESPONDENT);
        makeACall();
        callHandlerService.handleNextQueuedCall();

        createEmployee(EmployeeRequest.Role.MANAGER);
        makeACall();
        callHandlerService.handleNextQueuedCall();


        // GIVEN:
        EmployeeResponse employeeDirector = createEmployee(EmployeeRequest.Role.DIRECTOR);

        // WHEN:
        makeACall();
        callHandlerService.handleNextQueuedCall();


        // THEN:
        Employee director = employeeRepository.findById(employeeDirector.getId()).get();
        assertThat(director.getStatus(),is(Employee.Status.ON_CALL));
    }

    @Test
    public void givenNoEmployeeIsAvailableToAttendCallsThenCallsWillBeQueued() {
        createEmployee(EmployeeRequest.Role.RESPONDENT);
        createEmployee(EmployeeRequest.Role.MANAGER);
        createEmployee(EmployeeRequest.Role.DIRECTOR);
        IntStream.range(0,3).forEach(i -> makeACall());
        IntStream.range(0,3).forEach(i -> callHandlerService.handleNextQueuedCall());

        // making call when no employee is available
        IntStream.range(0,9).forEach(i -> makeACall());

        Properties queueProperties = rabbitAdmin.getQueueProperties(queueName);
        Integer queuedCalls = (Integer) queueProperties.get("QUEUE_MESSAGE_COUNT");

        assertThat(queuedCalls, is(9));

    }

    @Test
    public void givenAQueuedCallWhenEmployeeBecomesAvailableCallIsHandled() {
        createEmployee(EmployeeRequest.Role.RESPONDENT);
        createEmployee(EmployeeRequest.Role.MANAGER);
        createEmployee(EmployeeRequest.Role.DIRECTOR);

        List<String> callIds = Stream.generate(this::makeACall).limit(3).map(CallResponse::getId).collect(Collectors.toList());
        IntStream.range(0,3).forEach(i -> callHandlerService.handleNextQueuedCall());

        // making call when no employee is available
        int queuedCalls = 9;
        IntStream.range(0, queuedCalls).forEach(i -> makeACall());

        Properties beforeEnding = rabbitAdmin.getQueueProperties(queueName);
        Integer beforeEndingQueuedCalls = (Integer) beforeEnding.get("QUEUE_MESSAGE_COUNT");
        assertThat(beforeEndingQueuedCalls, is(queuedCalls));


        // ending an ongoing call
        callHandlerService.endCall(callIds.get(0));
        callHandlerService.handleNextQueuedCall();

        Properties afterEnding = rabbitAdmin.getQueueProperties(queueName);
        Integer afterEndingQueuedCalls = (Integer) afterEnding.get("QUEUE_MESSAGE_COUNT");
        assertThat(afterEndingQueuedCalls, is(queuedCalls - 1));
    }

}
