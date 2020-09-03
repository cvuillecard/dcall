package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.configuration.app.entity.message.Message;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.generic.cluster.vertx.AbstractContextVerticle;
import com.dcall.core.configuration.utils.URIUtils;
import com.dcall.core.configuration.generic.cluster.hazelcast.HazelcastCluster;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class InputConsumerVerticle extends AbstractContextVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputConsumerVerticle.class);

    @Override
    protected void setUriContext() {
        uriContext.setBaseRemoteAppUri(uriContext.getLocalUri("processor.vertx.command"));

        uriContext.setLocalConsumerUri(InputConsumerVerticle.class.getName());
        uriContext.setRemoteConsumerUri(uriContext.getRemoteUri("CommandProcessorConsumerVerticle"));
    }

    @Override
    public void start() {
        vertx.eventBus().consumer(URIUtils.getUri(uriContext.getLocalConsumerUri(), HazelcastCluster.getLocalUuid()), handler -> {
            try {
                LOG.info(" Terminal > received : \n" + handler.body().toString());
                final Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
                final byte[] bytes = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService().decryptMessage(runtimeContext, msg);

                GUIProcessor.bus().output().addToEntry(new String(bytes));

                handler.reply(" Terminal > data consumed.");
            }
            catch (Exception e) {
                handler.fail(-1, e.getMessage());
                GUIProcessor.bus().output().addToEntry(new String(e.getMessage()));
                LOG.error(e.getMessage());
            }
        });
    }
}
