package com.dcall.core.configuration.app.spring;

import com.dcall.core.configuration.app.context.RuntimeContext;
import org.springframework.context.annotation.Bean;

import org.springframework.orm.jpa.*;

//@Configuration
//@EnableTransactionManagement
//@ComponentScans(value = { @ComponentScan({ "com.dcall.core" }) })
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

    @Bean
    public RuntimeContext runtimeContext() {
        return new RuntimeContext();
    }
}