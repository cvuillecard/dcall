package com.dcall.core.configuration.app.context.cluster;

import com.dcall.core.configuration.app.context.fingerprint.FingerPrintContext;
import com.dcall.core.configuration.app.context.listener.ClusterListenerContext;
import com.dcall.core.configuration.app.context.user.UserContext;

import java.io.Serializable;

public class ClusterContext implements Serializable {
    private final UserContext userContext;
    private final FingerPrintContext fingerPrintContext = new FingerPrintContext();
    private final ClusterListenerContext clusterListenerContext = new ClusterListenerContext();

    public ClusterContext(final UserContext userContext) { this.userContext = userContext; }

    public UserContext userContext() { return userContext; }
    public FingerPrintContext fingerPrintContext() { return fingerPrintContext; }
    public ClusterListenerContext clusterListenerContext() { return clusterListenerContext; }
}
