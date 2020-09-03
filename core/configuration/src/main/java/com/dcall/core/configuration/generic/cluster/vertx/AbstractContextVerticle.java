package com.dcall.core.configuration.generic.cluster.vertx;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import io.vertx.core.AbstractVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractContextVerticle extends AbstractVerticle {
    @Autowired protected RuntimeContext runtimeContext;
    protected VertxURIContext uriContext = new VertxURIContext();

    protected AbstractContextVerticle() {
        setUriContext();
    }

    protected void setUriContext() {
        uriContext.setBaseLocalAppUri(this.getClass().getPackage().getName())
                .setBaseRemoteAppUri(uriContext.getBaseLocalAppUri())
                .setLocalConsumerUri(this.getClass().getName())
                .setRemoteConsumerUri(uriContext.getLocalConsumerUri());
    }
}
