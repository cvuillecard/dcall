package com.dcall.core.configuration.generic.cluster.vertx;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import io.vertx.core.AbstractVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public abstract class AbstractContextVerticle extends AbstractVerticle {
    protected final RuntimeContext runtimeContext;
    protected VertxURIContext uriContext = new VertxURIContext();

    protected AbstractContextVerticle(final RuntimeContext runtimeContext) {
        setUriContext();
        this.runtimeContext = runtimeContext;
    }

    protected void setUriContext() {
        uriContext.setBaseLocalAppUri(this.getClass().getPackage().getName())
                .setBaseRemoteAppUri(uriContext.getBaseLocalAppUri())
                .setLocalConsumerUri(this.getClass().getName())
                .setRemoteConsumerUri(uriContext.getLocalConsumerUri());
    }
}
