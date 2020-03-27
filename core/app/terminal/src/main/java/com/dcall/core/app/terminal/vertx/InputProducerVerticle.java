package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.vertx.constant.URIConfig;
import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InputProducerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputProducerVerticle.class);
    private final String input;

    public InputProducerVerticle(final String input) {
        this.input = input;
    }

    @Override
    public void start() {
        vertx.eventBus()
                .send(URIConfig.CMD_PROCESSOR_CONSUMER, input, res -> {
                    if (res.succeeded()) {
                        LOG.debug(" > GUI command traited by remote processor : replied > " + res.result());
                    } else {
                        LOG.error(this.getClass().getName() + " ERROR > " + res.cause().getMessage());
                    }
                });
    }
}
