package com.dcall.core.app.client.cli.vertx;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class InputConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputConsumerVerticle.class);

    @Override
    public void start()  {
        vertx.eventBus().consumer(InputConsumerVerticle.class.getName(), handler -> {
            LOG.info(" CLI > data received : \n" + handler.body().toString());

            handler.reply(" CLI > data consumed.");
        });
    }
}
