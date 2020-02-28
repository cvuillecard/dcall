package com.dcall.core.app.client;

import com.dcall.core.app.client.vertx.InputProducerVerticle;
import com.dcall.core.app.client.vertx.OutputConsumerVerticle;
import com.dcall.core.configuration.vertx.VertxApplication;

public final class Runner {

    public static void main(String[] args) {
        VertxApplication.start(OutputConsumerVerticle.class, InputProducerVerticle.class);
    }
}
