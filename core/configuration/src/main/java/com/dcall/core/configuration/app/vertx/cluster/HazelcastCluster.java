package com.dcall.core.configuration.app.vertx.cluster;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;

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
}
