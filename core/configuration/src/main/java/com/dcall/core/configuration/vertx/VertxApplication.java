package com.dcall.core.configuration.vertx;

import com.dcall.core.configuration.exception.TechnicalException;
import com.dcall.core.configuration.spring.JpaConfig;
import com.dcall.core.configuration.vertx.cluster.ClusterOptionsConfigurator;
import com.dcall.core.configuration.vertx.cluster.HazelcastConfigurator;
import com.dcall.core.configuration.vertx.ssl.VertxEventBusSSLConfigurator;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.VerticleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Classes that start the VertX application by deploying all verticles provided
 * in the {@link <T> VertxApplication#start(boolean, T[])} method
 */
public final class VertxApplication {
	private static final Logger LOG = LoggerFactory.getLogger(VertxApplication.class);
	private static ClusterOptionsConfigurator clusterOptionsConfigurator;
    private static VertxOptions options;
    private static final String FILE_PROPERTIES = "local.properties";
    private static final HazelcastConfigurator clusterManagerConfigurator = new HazelcastConfigurator();

    static {
        try {
            final Properties properties = loadProperties();

            clusterOptionsConfigurator = new ClusterOptionsConfigurator(configureVertxOptions(properties))
                    .configure(properties.get("cluster.host.ip").toString(),
                            properties.get("cluster.public.ip").toString(),
                            Integer.valueOf(properties.get("cluster.host.port").toString()),
                            Integer.valueOf(properties.get("cluster.public.port").toString()));

            options = clusterOptionsConfigurator.getVertxOptions();
        }
        catch (IOException e) {
            new TechnicalException(e).log();
        }
    }

    public static <T> void start(final boolean isSpringVerticle, T... verticles) {

        if (verticles == null) {
            throw new IllegalArgumentException("verticleToDeploy cannot be null");
        }

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                final Vertx vertx = res.result();
                final DeploymentOptions opts = new DeploymentOptions();
                final VerticleFactory verticleFactory = isSpringVerticle ? initSptring(vertx) : null;

                if (verticles instanceof Class[])
                    deployClasses(vertx, verticleFactory, opts, (Class<? extends Verticle>[]) verticles);
                else if (verticles instanceof Verticle[])
                    deployInstances(vertx, opts, (Verticle[]) verticles);
            }
        });
    }

    private static VertxOptions configureVertxOptions(final Properties properties) {
        return new VertxEventBusSSLConfigurator()
                .setKeyStoreFilePath(properties.get("keystore.path").toString())
                .setTrustStoreFilePath(properties.get("truststore.path").toString())
                .setPwdKeyStore(properties.get("keystore.pwd").toString())
                .setPwdTrustStore(properties.get("truststore.pwd").toString())
                .execute()
                .setClusterManager(clusterManagerConfigurator.configureDefault(properties));
                //.setClusterManager(new HazelcastConfigurator().configureNoMulticast(properties));

    }

//    public static void setHost(final String host, final String publicAddress) {
//        final String publicHost = publicAddress == null || publicAddress.isEmpty() ? host : publicAddress;
//
//        options.getEventBusOptions().setEventBusHost(host);
//        options.getEventBusOptions().setClusterPublicHost(publicHost);
//        options.setClusterHost(host);
//        options.setClusterPublicHost(publicHost);
//    }

    public static void setEventBusHost(final String host) {
        if (host != null && !host.isEmpty()) {
            options.getEventBusOptions().setHost(host);
            options.getEventBusOptions().setClusterPublicHost(host);
        }
    }

    public static void setClusterHost(final String host) {
        if (host != null && !host.isEmpty()) {
            options.setClusterHost(host);
            options.setClusterPublicHost(host);
        }
    }

    private static Properties loadProperties() throws IOException {
        final Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_PROPERTIES));

        return properties;
    }

    private static VerticleFactory initSptring(Vertx vertx) {
        final ApplicationContext context = new AnnotationConfigApplicationContext(JpaConfig.class);
        final VerticleFactory verticleFactory = context.getBean(SpringVerticleFactory.class);

        vertx.registerVerticleFactory(verticleFactory);

        return verticleFactory;
    }

    private static void deployClasses(final Vertx vertx, final VerticleFactory verticleFactory, final DeploymentOptions opts, final Class<? extends Verticle>[] verticles) {
        Arrays.stream(verticles).forEach(verticle -> {
            final String className = verticleFactory != null ? verticleFactory.prefix() + ':' + verticle.getName() : verticle.getName();

            vertx.deployVerticle(className, opts, handler -> {
                try {
                    if (handler.succeeded())
                        LOG.info(verticle.getName() + " verticle deployed");
                    else
                        throw new TechnicalException(VertxApplication.class.getName()
                                + " > Failed to deploy verticle : " + verticle.getName(), handler.cause());
                } catch (TechnicalException e) {
                    e.log();
                }
            });
        });
    }

    private static void deployInstances(final Vertx vertx, final DeploymentOptions opts, final Verticle[] verticles) {
        Arrays.stream(verticles).forEach(verticle -> vertx
                .deployVerticle(verticle, opts, handler -> {
                    try {
                        if (handler.succeeded())
                            LOG.info(verticle.getClass().getName() + " verticle deployed");
                        else
                            throw new TechnicalException(VertxApplication.class.getName()
                                    + " > Failed to deploy verticle : " + verticle.getClass().getName(), handler.cause());
                    } catch (TechnicalException e) {
                        e.log();
                    }
                }));
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

    public static void shutdown() {
        Vertx.currentContext().owner().close();
        LOG.debug("Local vertx context closed.");
    }
}