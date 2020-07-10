package com.dcall.core.configuration.generic.cluster.hazelcast;

import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.entity.cluster.Cluster;
import com.dcall.core.configuration.app.entity.cluster.ClusterBean;
import com.dcall.core.configuration.generic.cluster.hazelcast.listener.MemberClusterListener;
import com.hazelcast.config.Config;
import com.hazelcast.config.ListenerConfig;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class HazelcastConfigurator {
    private static final Logger LOG = LoggerFactory.getLogger(HazelcastConfigurator.class);
    private Config config = new Config();

    /**
     * Configure the network in a none-multicast mode,
     * which means tcp-ip must be enabled in this case.
     *
     * Note : configure Hazelcast for full TCP/IP cluster;
     *
     * @return {@link HazelcastConfigurator}
     */
    public HazelcastClusterManager configureNoMulticast(final String groupName, final String groupPass) {

        enableMulticast(false).enableTcpIp(true).configureGroup(groupName, groupPass);

        config.getNetworkConfig().setReuseAddress(true);

        return configureListeners().getClusterManager();
    }

    public HazelcastConfigurator configureGroup(final String groupName, final String groupPass) {
        final Cluster cluster = new ClusterBean(HashProvider.seedSha512((groupName + groupPass).getBytes(StandardCharsets.UTF_8)), groupName, groupPass);

        this.config.getGroupConfig().setName(cluster.getName());
        this.config.getGroupConfig().setPassword(cluster.getPassword()); // going to be deleted after test proved it's not used.

        return this;
    }

    public HazelcastConfigurator configureListeners() {
        final ListenerConfig listenerConfig = new ListenerConfig();

        this.addMemberClusterListener(listenerConfig);

        this.config.addListenerConfig(listenerConfig);

        return this;
    }

    private void addMemberClusterListener(final ListenerConfig listenerConfig) {
        final MemberClusterListener memberClusterListener = new MemberClusterListener();

        listenerConfig.setClassName(memberClusterListener.getClass().getName());
        listenerConfig.setImplementation(memberClusterListener);
    }

    private HazelcastClusterManager getClusterManager() {
        return new HazelcastClusterManager(this.config);
    }

    /**
     * configure "connection-timeout-seconds" for members joining cluster.
     *
     * Note : Default value is 5 seconds
     *
     * @param timeout
     * @return {@link HazelcastConfigurator}
     */
    private HazelcastConfigurator configureTimeout(final int timeout) {
        config.getNetworkConfig().getJoin().getTcpIpConfig().setConnectionTimeoutSeconds(timeout);

        return this;
    }

    private HazelcastConfigurator enableMulticast(final boolean enabled) {
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(enabled);

        return this;
    }

    private HazelcastConfigurator enableTcpIp(final boolean enabled) {
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(enabled);

        return this;
    }

    private HazelcastConfigurator setSocketInterceptor(final String className) {
        config.getNetworkConfig().getSocketInterceptorConfig().setEnabled(true);
        config.getNetworkConfig().getSocketInterceptorConfig().setClassName(className);

        return this;
    }

    /**
     * Add members to join configuration.
     *
     *  Note : All of the cluster members don't have to be listed there but at least one of them has to be active in cluster when a new member joins
     *
     * @param ipList
     * @return {@link HazelcastConfigurator}
     */
    public HazelcastConfigurator addTCPMembers(final String... ipList) {
        Arrays.stream(ipList).forEach(ip -> {
            final String[] address = ip.split(":");
            config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(ip);
            config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(address[0] + ":5701");
        });

        return this;
    }

    private HazelcastConfigurator configureInterfaces(final String... interfaces) {
        config.getNetworkConfig().getInterfaces().setEnabled(true);
        Arrays.stream(interfaces).forEach(ip -> config.getNetworkConfig().getInterfaces().addInterface(ip));

        return this;
    }
}
