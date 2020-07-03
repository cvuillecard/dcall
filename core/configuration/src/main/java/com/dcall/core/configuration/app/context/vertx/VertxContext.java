package com.dcall.core.configuration.app.context.vertx;

import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;

public final class VertxContext {
    private VertxURIContext vertxURIContext = new VertxURIContext();

    public VertxURIContext getVertxURIContext() { return vertxURIContext; }
}
