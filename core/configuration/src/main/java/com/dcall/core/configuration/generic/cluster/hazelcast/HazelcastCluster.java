package com.dcall.core.configuration.generic.cluster.hazelcast;

import com.dcall.core.configuration.generic.cluster.hazelcast.listener.MemberClusterListener;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.*;

import java.util.Set;

public final class HazelcastCluster {
    private static HazelcastInstance instance;

    public static HazelcastInstance getInstance() {
        if (instance == null)
            instance = Hazelcast.getAllHazelcastInstances().iterator().next();

        return instance;
    }

    public static Cluster getCluster() {
        return HazelcastCluster.getInstance().getCluster();
    }

    public static Set<Member> getMembers() {
        return HazelcastCluster.getCluster().getMembers();
    }

    public static Member getLocalMember() {
        return HazelcastCluster.getCluster().getLocalMember();
    }

    public static String getLocalUuid() {
        return HazelcastCluster.getLocalMember().getUuid();
    }

    public static void shutdown() { getInstance().shutdown(); }

    public static <T> T getListener(final String className) {
        for (final ListenerConfig conf : getInstance ().getConfig().getListenerConfigs()) {
            if (conf.getImplementation().getClass().getName().equals(MemberClusterListener.class.getName()))
                return (T) conf.getImplementation();
        }

        return null;
    }

    public static MemberClusterListener getMemberShipListener() { return getListener(MemberClusterListener.class.getName()); }
}
