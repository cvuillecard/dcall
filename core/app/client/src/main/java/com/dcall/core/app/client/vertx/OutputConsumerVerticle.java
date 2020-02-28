package com.dcall.core.app.client.vertx;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class OutputConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(OutputConsumerVerticle.class);

    @Override
    public void start() {
        vertx.eventBus().consumer(OutputConsumerVerticle.class.getSimpleName(), handler -> {
            LOG.info(handler.body().toString());
            handler.reply("done");
        });
    }
}
