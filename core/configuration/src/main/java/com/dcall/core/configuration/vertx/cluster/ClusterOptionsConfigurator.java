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

        this.eventBusOptions.setClustered(true);
    }

    public ClusterOptionsConfigurator configure(final String hostIp, final int port) {
        final String host = hostIp != null && !hostIp.isEmpty() ? hostIp : HostUtils.getLocalHostIp();

        return setEventBusHost(host).configurePort(port);
    }

    private ClusterOptionsConfigurator configurePort(final int port) {
        if (port > 0)
            eventBusOptions.setPort(port);

        return this;
    }

    private ClusterOptionsConfigurator setEventBusHost(final String host) {
        if (host != null && !host.isEmpty())
            eventBusOptions.setHost(host);

        return this;
    }

    // GETTERS
    public VertxOptions getVertxOptions() { return vertxOptions; }
    public EventBusOptions getEventBusOptions() { return eventBusOptions; }
}
