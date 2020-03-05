package com.dcall.core.configuration.vertx.cluster;

import com.hazelcast.config.Config;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public final class HazelcastConfigurator {
    private static final Logger LOG = LoggerFactory.getLogger(HazelcastConfigurator.class);
    private Config config = new Config();

    public HazelcastClusterManager configure(final Properties properties) {
        configureTcp(properties.get("hazelcast.join.ip").toString());
//        this.configureMultiCast(false);

        return getClusterManager();
    }

    private HazelcastClusterManager getClusterManager() {
        return new HazelcastClusterManager(this.config);
    }

    private HazelcastConfigurator configureMultiCast(final boolean enabled) {
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(enabled);

        return this;
    }

    private HazelcastConfigurator configureTcp(final String joinIp) {
        config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(joinIp);

        return this;
    }
}
