package com.dcall.core.configuration.app.context.route;

import com.dcall.core.configuration.app.context.vertx.VertxContext;

import java.io.Serializable;

public final class RouteContext implements Serializable {
    private VertxContext vertxContext = new VertxContext();

    public VertxContext getVertxContext() { return vertxContext; }
}
