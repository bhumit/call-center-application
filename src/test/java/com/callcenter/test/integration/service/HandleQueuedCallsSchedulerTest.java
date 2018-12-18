package com.callcenter.test.integration.service;

import com.callcenter.service.CallHandlerService;
import org.awaitility.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HandleQueuedCallsSchedulerTest {

    @SpyBean
    private CallHandlerService myTask;

    @Test
    public void jobRuns() {
        await().atMost(Duration.FIVE_SECONDS)
            .untilAsserted(() -> verify(myTask, times(1)).handleNextQueuedCall());
    }
}

