package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.app.terminal.vertx.constant.URIConfig;
import com.dcall.core.configuration.bo.Message;
import com.dcall.core.configuration.entity.MessageBean;
import io.vertx.core.AbstractVerticle;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class InputConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputConsumerVerticle.class);

    @Override
    public void start() {
        vertx.eventBus().consumer(URIConfig.URI_CLIENT_TERMINAL_CONSUMER, handler -> {
            LOG.info(" Terminal > data received : \n" + handler.body().toString());
            final Message<String> resp = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
            GUIProcessor.bus().output().addToEntry(new String(resp.getMessage()));
            handler.reply(" Terminal > data consumed.");
        });
    }
}
