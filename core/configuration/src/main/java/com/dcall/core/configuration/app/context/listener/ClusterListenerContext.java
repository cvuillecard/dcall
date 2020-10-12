package com.dcall.core.configuration.app.context.listener;

import com.dcall.core.configuration.generic.cluster.hazelcast.listener.MemberClusterListener;

import java.io.Serializable;

public final class ClusterListenerContext implements Serializable {
    private MemberClusterListener memberClusterListener;

    public MemberClusterListener getMemberClusterListener() { return memberClusterListener; }

    public ClusterListenerContext setMemberClusterListener(final MemberClusterListener memberClusterListener) {
        this.memberClusterListener = memberClusterListener;

        return this;
    }

}
