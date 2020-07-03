package com.dcall.core.configuration.app.verticle.fingerprint;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class FingerPrintConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(FingerPrintConsumerVerticle.class);
    @Autowired private RuntimeContext runtimeContext;

    private VertxURIContext uriContext() {
        final VertxURIContext uriContext = new VertxURIContext();

        return uriContext.setBaseLocalAppUri(this.getClass().getPackage().getName())
                .setBaseRemoteAppUri(uriContext.getBaseLocalAppUri())
                .setLocalConsumerUri(this.getClass().getName())
                .setRemoteConsumerUri(uriContext.getLocalConsumerUri());
    }

    @Override
    public void start() {
        final MessageConsumer<Object> consumer = vertx.eventBus().consumer(uriContext().getLocalConsumerUri());

        consumer.handler(handler -> {

        });
    }
}
