package com.dcall.core.configuration.vertx.cluster;

import com.dcall.core.configuration.utils.HostUtils;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClusterOptionsConfigurator {
    private static final Logger LOG = LoggerFactory.getLogger(ClusterOptionsConfigurator.class);

    private VertxOptions vertxOptions = new VertxOptions();
    private EventBusOptions eventBusOptions = new EventBusOptions();

    public ClusterOptionsConfigurator(final VertxOptions vertxOptions) {
        this.vertxOptions = vertxOptions;
        this.eventBusOptions = vertxOptions.getEventBusOptions();
    }

    public ClusterOptionsConfigurator configure(final String hostIp, final String publicIp, final Integer eventBusPort, final Integer clusterPort) {
        final String host = hostIp != null && !hostIp.isEmpty() ? hostIp : HostUtils.getLocalHostIp();
        final String publicHost = publicIp != null && !publicIp.isEmpty() ? publicIp : host;

        return setEventBusHost(host, publicHost).setClusterHost(host, publicHost)
                .configurePort(eventBusPort, clusterPort);
    }

    private ClusterOptionsConfigurator configurePort(final Integer eventBusPort, final Integer clusterPort) {
        if (eventBusPort != null) {
            eventBusOptions.setPort(eventBusPort);
        }
        if (clusterPort != null) {
            if (clusterPort.equals(eventBusPort))
                throw new IllegalArgumentException(this.getClass().getName() + " ERROR > eventBusPort must be different of publicPort");
            vertxOptions.setClusterPort(clusterPort);
        }

        return this;
    }

    private ClusterOptionsConfigurator setClusterHost(final String clusterHostIp, final String publicHostIp) {
        vertxOptions.setClusterHost(clusterHostIp);
        vertxOptions.setClusterPublicHost(publicHostIp);

        return this;
    }

    private ClusterOptionsConfigurator setEventBusHost(final String host, final String publicHost) {
        eventBusOptions.setHost(host);
        eventBusOptions.setClusterPublicHost(publicHost);

        return this;
    }

    // GETTERS
    public VertxOptions getVertxOptions() { return vertxOptions; }
    public EventBusOptions getEventBusOptions() { return eventBusOptions; }
}
