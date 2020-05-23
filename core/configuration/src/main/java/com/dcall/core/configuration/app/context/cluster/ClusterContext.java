package com.dcall.core.configuration.app.context.cluster;

import com.dcall.core.configuration.app.context.user.UserContext;

public class ClusterContext {
    private final UserContext userContext = new UserContext();

    public UserContext userContext() { return userContext; }
}
