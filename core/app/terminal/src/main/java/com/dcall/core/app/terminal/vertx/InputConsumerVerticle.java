package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.app.terminal.vertx.constant.URIConfig;
import com.dcall.core.configuration.entity.message.Message;
import com.dcall.core.configuration.entity.message.MessageBean;
import com.dcall.core.configuration.utils.URIUtils;
import com.dcall.core.configuration.vertx.cluster.HazelcastCluster;
import io.vertx.core.AbstractVerticle;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InputConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputConsumerVerticle.class);

    @Override
    public void start() {
        vertx.eventBus().consumer(URIUtils.getUri(URIConfig.URI_CLIENT_TERMINAL_CONSUMER, HazelcastCluster.getLocalUuid()), handler -> {
            LOG.info(" Terminal > data received : \n" + handler.body().toString());
            final Message<String> resp = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
            GUIProcessor.bus().output().addToEntry(new String(resp.getMessage()));
            handler.reply(" Terminal > data consumed.");
        });
    }
}
