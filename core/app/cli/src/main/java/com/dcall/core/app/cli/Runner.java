package com.dcall.core.app.cli;

import com.dcall.core.configuration.app.vertx.VertxApplication;

import com.dcall.core.app.cli.vertx.InputConsumerVerticle;
import com.dcall.core.app.cli.vertx.InputProducerVerticle;
import com.dcall.core.app.cli.vertx.OutputConsumerVerticle;

public final class Runner {

    public static void main(final String[] args) {
            VertxApplication.startOnCluster(
                    true,
                    args,
                    InputConsumerVerticle.class,
                    OutputConsumerVerticle.class,
                    InputProducerVerticle.class
            );
    }
}
