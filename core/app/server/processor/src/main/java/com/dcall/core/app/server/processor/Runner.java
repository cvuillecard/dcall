package com.dcall.core.app.server.processor;

import com.dcall.core.app.server.processor.vertx.command.CommandProcessorConsumerVerticle;
import com.dcall.core.configuration.vertx.VertxApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Runner {
    private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

    public static void main(final String[] args) {
        VertxApplication.startOnCluster(
                true,
                 args,
                 CommandProcessorConsumerVerticle.class
        );
    }
}
