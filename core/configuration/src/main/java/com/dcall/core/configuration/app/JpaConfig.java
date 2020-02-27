package com.dcall.core.configuration.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

import org.springframework.orm.jpa.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScans(value = { @ComponentScan({ "com.dcall.core" }) })
public class JpaConfig {
    @Bean
    public LocalEntityManagerFactoryBean getEntityManagerFactoryBean() {
        final LocalEntityManagerFactoryBean factoryBean = new LocalEntityManagerFactoryBean();

        factoryBean.setPersistenceUnitName("LOCAL_PERSISTENCE");

        return factoryBean;
    }

    @Bean
    public JpaTransactionManager getJpaTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(getEntityManagerFactoryBean().getObject());

        return transactionManager;
    }
}