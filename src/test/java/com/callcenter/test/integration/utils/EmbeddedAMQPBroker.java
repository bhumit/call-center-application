package com.callcenter.test.integration.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.qpid.server.SystemLauncher;
import org.junit.rules.ExternalResource;

public class EmbeddedAMQPBroker extends ExternalResource {

    private final SystemLauncher broker = new SystemLauncher();

    @Override
    protected void before() throws Throwable {
        startQpidBroker();
    }

    @Override
    protected void after() {
        broker.shutdown();
    }

    private void startQpidBroker() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("type", "Memory");
        attributes.put("initialConfigurationLocation", findResourcePath("initial-config.json"));
        broker.startup(attributes);
    }

    private String findResourcePath(final String fileName) {
        return EmbeddedAMQPBroker.class.getClassLoader().getResource(fileName).toExternalForm();
    }
}
