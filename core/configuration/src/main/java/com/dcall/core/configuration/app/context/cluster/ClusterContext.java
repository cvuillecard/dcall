package com.dcall.core.configuration.app.context.cluster;

import com.dcall.core.configuration.app.context.fingerprint.FingerPrintContext;
import com.dcall.core.configuration.app.context.user.UserContext;

public class ClusterContext {
    private final UserContext userContext;
    private final FingerPrintContext fingerPrintContext = new FingerPrintContext();

    public ClusterContext(final UserContext userContext) { this.userContext = userContext; }

    public UserContext userContext() { return userContext; }
    public FingerPrintContext fingerPrintContext() { return fingerPrintContext; }
}
