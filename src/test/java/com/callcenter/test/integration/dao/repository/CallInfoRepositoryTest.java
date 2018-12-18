package com.callcenter.test.integration.dao.repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import com.callcenter.dao.model.CallInfo;
import com.callcenter.dao.repository.CallInfoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataMongoTest
@TestPropertySource("classpath:integration-test.properties")
public class CallInfoRepositoryTest {

    @Autowired
    private CallInfoRepository callInfoRepository;

    @Test
    public void testCreateCallInfo() {
        CallInfo callInfo = callInfoRepository.save(generateCallInfo());
        assertNotNull(callInfo);
        assertNotNull(callInfo.getId());
    }

    @Test
    public void testFindQueuedCalls() {
        IntStream.range(0,4).forEach(i -> callInfoRepository.save(generateCallInfo()));

        Page<CallInfo> callInfoPage = callInfoRepository.findCallInfoByisQueuedTrueOrderByReceivedAt(Pageable.unpaged());
        List<CallInfo> callInfos = callInfoPage.getContent();
        assertThat(callInfos.size(), is(5));
    }

    private CallInfo generateCallInfo() {
        CallInfo callInfo = new CallInfo();
        callInfo.setEmployeeId(UUID.randomUUID().toString());
        callInfo.setInProgress(false);
        callInfo.setCallerId("+2222");
        callInfo.setCallerName("Test");
        callInfo.setQueued(true);
        return callInfo;
    }
}
