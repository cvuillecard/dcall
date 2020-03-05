package com.dcall.core.app.server.processor;

import com.dcall.core.app.server.processor.vertx.command.CommandProcessorConsumerVerticle;
import com.dcall.core.configuration.vertx.VertxApplication;

public final class Runner {

    public static void main(final String[] args) {
        VertxApplication.start(
                true,
                 CommandProcessorConsumerVerticle.class
        );
    }
}
