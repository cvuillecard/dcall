package com.dcall.core.app.client.cli;

import com.dcall.core.configuration.vertx.VertxApplication;

import com.dcall.core.app.client.cli.vertx.InputConsumerVerticle;
import com.dcall.core.app.client.cli.vertx.InputProducerVerticle;
import com.dcall.core.app.client.cli.vertx.OutputConsumerVerticle;

public final class Runner {

    public static void main(final String[] args) {
            VertxApplication.start(
                    true,
                    InputConsumerVerticle.class,
                    OutputConsumerVerticle.class,
                    InputProducerVerticle.class
            );
    }
}
