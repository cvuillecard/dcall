package com.dcall.core.configuration.vertx;

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

    public ClusterOptionsConfigurator configure(final String localIp, final String publicIp, final Integer localPort, final String publicPort) {
        final String host = localIp != null && !localIp.isEmpty() ? localIp : HostUtils.getLocalHostIp();
        final String publicHost = publicIp != null && !publicIp.isEmpty() ? publicIp : host;

        return setEventBusHost(host, publicHost).setVertxBusHost(host, publicHost)
                .configurePort(localPort, publicPort);
    }

    private ClusterOptionsConfigurator configurePort(final Integer port, final String publicPort) {
        if (port != null) {
            vertxOptions.setClusterPort(port);
        }
        if (publicPort != null && !publicPort.isEmpty()) {
            if (Integer.valueOf(publicPort).equals(port))
                throw new IllegalArgumentException(this.getClass().getName() + " ERROR > localPort must be different of publicPort");
            vertxOptions.setClusterPublicHost(publicPort);
        }

        return this;
    }

    private ClusterOptionsConfigurator setVertxBusHost(final String clusterHostIp, final String publicHostIp) {
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
