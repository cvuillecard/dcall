package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import io.vertx.core.AbstractVerticle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InputConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputConsumerVerticle.class);

    @Override
    public void start() {
        vertx.eventBus().consumer(InputConsumerVerticle.class.getName(), handler -> {
            LOG.info(" Terminal > data received : \n" + handler.body().toString());
            GUIProcessor.bus().output().addToEntry(handler.body().toString());
            handler.reply(" Terminal > data consumed.");
        });
    }
}
