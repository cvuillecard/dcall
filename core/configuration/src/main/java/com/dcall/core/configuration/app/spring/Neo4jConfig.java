package com.dcall.core.configuration.app.spring;

import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

//@Configuration
//@EnableTransactionManagement
//@ComponentScans(value = { @ComponentScan({ "com.dcall.core" }) })
public class Neo4jConfig {
	@Bean
	public SessionFactory getSessionFactory() {
		return new SessionFactory(configuration(), "com.dcall.core.app");
	}

	@Bean
	public Neo4jTransactionManager transactionManager() {
		return new Neo4jTransactionManager(getSessionFactory());
	}

	@Bean
	public Session getSession() {
		return getSessionFactory().openSession();
	}

	@Bean
	public org.neo4j.ogm.config.Configuration configuration() {
		return new org.neo4j.ogm.config.Configuration.Builder(new ClasspathConfigurationSource("neo4j.properties"))
				.build();
	}
}
