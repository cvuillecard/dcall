package com.dcall.core.configuration.app.context.vertx;

import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;

import java.io.Serializable;

public final class VertxContext implements Serializable {
    private VertxURIContext vertxURIContext = new VertxURIContext();

    public VertxURIContext getVertxURIContext() { return vertxURIContext; }

    public void setVertxURIContext(final VertxURIContext vertxURIContext) { this.vertxURIContext = vertxURIContext; }
}
