package com.dcall.core.configuration.spring.app;

import com.dcall.core.configuration.context.RuntimeContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScans(value = { @ComponentScan({ "com.dcall.core" }) })
public class SpringConfig {
    @Bean public RuntimeContext runtimeContext() { return new RuntimeContext(); }
}
