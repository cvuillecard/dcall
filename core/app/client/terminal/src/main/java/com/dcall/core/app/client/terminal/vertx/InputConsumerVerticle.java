package com.dcall.core.app.client.terminal.vertx;

import com.dcall.core.app.client.terminal.gui.GUIProcessor;
import com.dcall.core.app.client.terminal.gui.controller.display.DisplayController;
import io.vertx.core.AbstractVerticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InputConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputConsumerVerticle.class);

    @Override
    public void start() {
        vertx.eventBus().consumer(InputConsumerVerticle.class.getName(), handler -> {
            LOG.info(" > data received : \n" + handler.body().toString());
            GUIProcessor.bus().output().addToEntry(handler.body().toString());
        });
    }
}
