package com.dcall.core.configuration.spring.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScans(value = { @ComponentScan({ "com.dcall.core" }) })
public class SpringConfig {
    @Bean
    public SharedData sharedData() {
        return new SharedData();
    }
}
