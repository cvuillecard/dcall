package com.dcall.core.configuration.app.vertx;

import com.dcall.core.configuration.app.constant.ConstantResource;
import com.dcall.core.configuration.app.exception.TechnicalException;
import com.dcall.core.configuration.app.spring.SpringConfig;
import com.dcall.core.configuration.utils.ResourceUtils;
import com.dcall.core.configuration.app.vertx.cluster.ClusterOptionsConfigurator;
import com.dcall.core.configuration.app.vertx.cluster.HazelcastConfigurator;
import com.dcall.core.configuration.app.vertx.ssl.VertxEventBusSSLConfigurator;
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
    private static HazelcastConfigurator hazelcastConfigurator;

    public static void init(final String host, final Integer port) {
        final String hostName = host != null && !host.isEmpty() ? host : ResourceUtils.getString("cluster.host.ip");
        final int hostPort = port != null && port > 0 ? port : ResourceUtils.getInt("cluster.host.port");

        hazelcastConfigurator = new HazelcastConfigurator();
        clusterOptionsConfigurator = new ClusterOptionsConfigurator(configureVertxOptions(ResourceUtils.localProperties()))
                .configure(hostName, hostPort);

        options = clusterOptionsConfigurator.getVertxOptions();
    }

    public static <T> void startOnCluster(final boolean isSpringVerticle, final String[] peers, T... verticles) {
        if (verticles == null) {
            throw new IllegalArgumentException("verticleToDeploy cannot be null");
        }

        if (hazelcastConfigurator == null)
            init(null, null);

        if (peers.length < 1) {
            throw new IllegalArgumentException("Bad usage of 'peers' parameter : Need at least one peer endpoint to join the cluster where peer is a string as \"ip:port\" ");
        }

        hazelcastConfigurator.addTCPMembers(peers);
        start(isSpringVerticle, verticles);
    }

    public static <T> void start(final boolean isSpringVerticle, T... verticles) {

        if (verticles == null) {
            throw new IllegalArgumentException("verticleToDeploy cannot be null");
        }

        if (hazelcastConfigurator == null)
            init(null, null);

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                final Vertx vertx = res.result();
                final DeploymentOptions opts = new DeploymentOptions();
                final VerticleFactory verticleFactory = isSpringVerticle ? initSpring(vertx) : null;

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
                .setClusterManager(hazelcastConfigurator.configureNoMulticast());
    }

    private static Properties loadProperties() throws IOException {
        final Properties properties = new Properties();
        properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(ConstantResource.LOCAL_PROPERTIES));

        return properties;
    }

    private static VerticleFactory initSpring(Vertx vertx) {
        final ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
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
