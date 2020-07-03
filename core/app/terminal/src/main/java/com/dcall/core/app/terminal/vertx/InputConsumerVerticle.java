package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import com.dcall.core.configuration.app.entity.message.Message;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.utils.URIUtils;
import com.dcall.core.configuration.generic.vertx.cluster.HazelcastCluster;
import io.vertx.core.AbstractVerticle;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class InputConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputConsumerVerticle.class);

    @Autowired private RuntimeContext runtimeContext;

    private VertxURIContext configureURI() {
        final VertxURIContext uriContext = this.runtimeContext.systemContext().routeContext().getVertxContext().getVertxURIContext();

        uriContext.setBaseRemoteAppUri(uriContext.getLocalUri("processor.vertx.command"));

        uriContext.setLocalConsumerUri(InputConsumerVerticle.class.getName());
        uriContext.setRemoteConsumerUri(uriContext.getRemoteUri("local.LocalCommandProcessorConsumerVerticle"));

        return uriContext;
    }


    @Override
    public void start() {
        vertx.eventBus().consumer(URIUtils.getUri(configureURI().getLocalConsumerUri(), HazelcastCluster.getLocalUuid()), handler -> {
            LOG.info(" Terminal > data received : \n" + handler.body().toString());
            final Message<String> resp = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
            GUIProcessor.bus().output().addToEntry(new String(resp.getMessage()));
            handler.reply(" Terminal > data consumed.");
        });
    }
}
