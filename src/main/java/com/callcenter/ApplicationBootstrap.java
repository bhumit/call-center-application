package com.callcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class ApplicationBootstrap extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationBootstrap.class, args);
    }

    @Configuration
    @ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true", matchIfMissing = true)
    @EnableScheduling
    class SchedulingConfiguration {
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ApplicationBootstrap.class);
    }

    @Bean
    public RabbitMQConfiguration rabbitMQConfiguration() {
        return new RabbitMQConfiguration();
    }

}