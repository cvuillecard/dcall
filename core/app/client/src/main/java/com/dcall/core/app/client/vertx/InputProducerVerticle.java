package com.dcall.core.app.client.vertx;

import com.dcall.core.configuration.vertx.VertxApplication;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class InputProducerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputProducerVerticle.class);
    private String input;

    @Override
    public void start() {
        this.input = "TEST";
        MessageProducer<String> producer = vertx.eventBus().sender(OutputConsumerVerticle.class.getSimpleName());
        producer.send(input, handler -> {
            if (handler.succeeded()) {
                LOG.info(handler.result().body().toString());
            }
        });
    }
}
