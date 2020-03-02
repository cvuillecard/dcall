package com.dcall.core.app.client;

import com.dcall.core.app.client.vertx.InputConsumerVerticle;
import com.dcall.core.app.client.vertx.InputProducerVerticle;
import com.dcall.core.app.client.vertx.OutputConsumerVerticle;
import com.dcall.core.configuration.vertx.VertxApplication;

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