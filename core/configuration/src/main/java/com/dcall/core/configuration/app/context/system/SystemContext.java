package com.dcall.core.configuration.app.context.system;

import com.dcall.core.configuration.app.context.route.RouteContext;
import com.dcall.core.configuration.app.context.version.VersionContext;

import java.io.Serializable;

public final class SystemContext implements Serializable {
    private RouteContext routeContext = new RouteContext();
    private VersionContext versionContext = new VersionContext();

    public RouteContext routeContext() { return routeContext; }
    public VersionContext versionContext() { return versionContext; }
}
