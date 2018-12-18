package com.callcenter.test.integration;

import java.util.UUID;

import com.callcenter.dao.repository.CallInfoRepository;
import com.callcenter.dao.repository.EmployeeRepository;
import com.callcenter.service.CallHandlerService;
import com.callcenter.service.EmployeeService;
import com.callcenter.controller.request.CallRequest;
import com.callcenter.controller.request.EmployeeRequest;
import com.callcenter.controller.response.CallResponse;
import com.callcenter.controller.response.EmployeeResponse;
import com.callcenter.test.integration.utils.EmbeddedAMQPBroker;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:integration-test.properties")
public abstract class BaseIntegrationTest {

    @ClassRule
    public static EmbeddedAMQPBroker embeddedAMQPBroker = new EmbeddedAMQPBroker();

    @Autowired
    protected CallHandlerService callHandlerService;

    @Autowired
    protected EmployeeService employeeService;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected CallInfoRepository callInfoRepository;

    @Autowired
    protected MongoOperations mongoOperations;

    @Configuration
    public static class Config {
        @Primary
        @Bean
        ServletWebServerFactory servletWebServerFactory(){
            return new TomcatServletWebServerFactory();
        }
    }

    protected EmployeeResponse createEmployee(EmployeeRequest.Role role) {
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName("Jane");
        employeeRequest.setLastName(UUID.randomUUID().toString());
        employeeRequest.setRole(role);

        return employeeService.createEmployee(employeeRequest);
    }

    protected CallResponse makeACall() {
        CallRequest callRequest = new CallRequest();
        callRequest.setCallerId(RandomStringUtils.randomNumeric(10));
        callRequest.setCallerName("Test User " + RandomStringUtils.randomNumeric(4));
        return callHandlerService.queueCall(callRequest);
    }



}
