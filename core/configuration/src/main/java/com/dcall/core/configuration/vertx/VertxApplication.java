package com.dcall.core.configuration.vertx;

import com.dcall.core.configuration.exception.TechnicalException;
import com.dcall.core.configuration.spring.JpaConfig;
import com.dcall.core.configuration.vertx.ssl.VertxEventBusSSLConfigurator;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Classes that start the VertX application by deploying all verticles provided
 * in the {@link VertxApplication#start(Class[])} method
 */
public final class VertxApplication {

	private static final Logger LOG = LoggerFactory.getLogger(VertxApplication.class);

	public static void start(Class<? extends Verticle>... verticles) {

		if (verticles == null) {
			throw new IllegalArgumentException("verticleToDeploy cannot be null");
		}

		try {
			final Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("local.properties"));
			final ClusterManager mgr = new HazelcastClusterManager();
			final VertxOptions options = new VertxEventBusSSLConfigurator()
					.setKeyStoreFilePath(properties.get("keystore.path").toString())
					.setTrustStoreFilePath(properties.get("truststore.path").toString())
					.setPwdKeyStore(properties.get("keystore.pwd").toString())
					.setPwdTrustStore(properties.get("truststore.pwd").toString()).execute().setClusterManager(mgr);

            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    final Vertx vertx = res.result();
                    ApplicationContext context = new AnnotationConfigApplicationContext(JpaConfig.class);
                    VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);

                    vertx.registerVerticleFactory(verticleFactory);

                    DeploymentOptions opts = new DeploymentOptions();


                    Arrays.stream(verticles).forEach(verticle -> vertx
                            .deployVerticle(verticleFactory.prefix() + ':' + verticle.getName(), opts, handler -> {
                                try {
                                    if (handler.succeeded())
                                        LOG.info(verticle.getName() + " verticle deployed");
                                    else
                                        throw new TechnicalException(VertxApplication.class.getName()
                                                + " > Failed to deploy verticle : " + verticle.getName(), handler.cause());
                                } catch (TechnicalException e) {
                                    e.log();
                                }
                            }));
                }
            });
		} catch (IOException e) {
			new TechnicalException(e).log();
		}
	}

    public static void stop(Class<? extends Verticle>... verticles) {
        if (verticles == null) {
            throw new IllegalArgumentException("verticleToDeploy cannot be null");
        }

        Arrays.stream(verticles).forEach(verticle ->
                Vertx.currentContext().owner()
                        .undeploy(verticle.getClass().getName(), res -> {
                            try {
                                if (res.succeeded())
                                    LOG.info(verticle.getName() + " verticle undeployed");
                                else
                                    throw new TechnicalException(VertxApplication.class.getName()
                                            + " > Failed to undeploy verticle : " + verticle.getName(), res.cause());
                            } catch (TechnicalException e) {
                                e.log();
                            }
                        }));
    }
}