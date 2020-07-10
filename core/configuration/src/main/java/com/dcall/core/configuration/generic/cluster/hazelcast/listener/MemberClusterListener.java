package com.dcall.core.configuration.generic.cluster.hazelcast.listener;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.WithRuntimeContext;
import com.dcall.core.configuration.app.context.fingerprint.FingerPrintContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MemberClusterListener implements MembershipListener, WithRuntimeContext {
    private static final Logger LOG = LoggerFactory.getLogger(MembershipListener.class);
    private RuntimeContext runtimeContext;

    @Override
    public void memberAdded(final MembershipEvent membershipEvent) {
        final String memberId = membershipEvent.getMember().getUuid();
        LOG.debug("Member added : " + memberId);
    }

    @Override
    public void memberRemoved(final MembershipEvent membershipEvent) {
        final String memberId = membershipEvent.getMember().getUuid();
        removeMemberFromContext(memberId);
        LOG.debug("Member removed : " + memberId);
    }

    private void removeMemberFromContext(final String memberId) {
        if (runtimeContext != null) {
            final FingerPrintContext fingerPrintContext = runtimeContext.clusterContext().fingerPrintContext();
            final FingerPrint fingerPrint = fingerPrintContext.getFingerprints().get(memberId);

            if (fingerPrint != null) {
                if (fingerPrintContext.current() != null && fingerPrint.getId().equals(fingerPrintContext.current().getId()))
                    fingerPrintContext.setCurrent(null);
                fingerPrintContext.getFingerprints().remove(memberId);
                LOG.debug("Member removed from context : " + memberId);
            }
        }
        else
            LOG.warn("No runtime context available : " + memberId + " may still in cluster context");
    }

    @Override
    public void memberAttributeChanged(final MemberAttributeEvent memberAttributeEvent) {
        LOG.debug("Member Attribute changes : " + memberAttributeEvent.getMember().getUuid());
    }

    @Override
    public RuntimeContext getContext() { return this.runtimeContext; }

    @Override
    public MemberClusterListener setContext(final RuntimeContext context) {
        this.runtimeContext = context;

        return this;
    }
}
