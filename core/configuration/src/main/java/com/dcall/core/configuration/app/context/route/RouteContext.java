package com.dcall.core.configuration.app.context.route;

import com.dcall.core.configuration.app.context.vertx.VertxContext;

public final class RouteContext {
    private VertxContext vertxContext = new VertxContext();

    public VertxContext getVertxContext() { return vertxContext; }
}
