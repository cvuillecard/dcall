package com.dcall.core.app.processor.vertx.command.local;

import com.dcall.core.app.processor.bean.local.controller.command.LocalCommandController;
import com.dcall.core.app.processor.vertx.command.CommandProcessorConsumerVerticle;
import com.dcall.core.app.processor.vertx.constant.URIConfig;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.generic.entity.message.MessageBean;
import com.dcall.core.configuration.app.exception.TechnicalException;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.utils.URIUtils;
import com.dcall.core.configuration.app.vertx.cluster.HazelcastCluster;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class LocalCommandProcessorConsumerVerticle extends AbstractVerticle {
    @Autowired private RuntimeContext runtimeContext;
    private static final Logger LOG = LoggerFactory.getLogger(LocalCommandProcessorConsumerVerticle.class);

    private final int BUF_SIZE = 8192;
    private final LocalCommandController commandController = new LocalCommandController();

    private void execute(final Message<Object> handler, final com.dcall.core.configuration.generic.entity.message.Message<String> msg) {
        if (handler != null) {
            try {
                handleLocalCommand(handler, msg);
            } catch (Exception e) {
                handleError(handler, e.getMessage(), msg);
            }
        }
    }

    private void handleLocalCommand(final Message<Object> handler, final com.dcall.core.configuration.generic.entity.message.Message<String> sender) {
        vertx.executeBlocking(future -> {
            try {
                final com.dcall.core.configuration.generic.entity.message.Message<String> resp = new MessageBean(HazelcastCluster.getLocalUuid(), null, 0);

                final byte[] result = commandController.execute(this.runtimeContext, new String(sender.getMessage()));

                sendChunk(URIConfig.URI_CLIENT_TERMINAL_CONSUMER, sender, result, getNbChunk(result), resp);
            }
            catch (Exception e) {
                handleError(handler, e.getMessage(), sender);
                LOG.error(e.getMessage());
            }
            finally {
                future.complete();
            }
        }, res -> {
            if (res.succeeded()) {
                try {
                    handler.reply(this.getClass().getSimpleName() + " > SUCCESS");
                } catch (Exception e) {
                    LOG.error(e.getMessage());
                }
            } else {
                LOG.debug(this.getClass().getSimpleName() + " > FAILURE");
                handler.reply(res.cause());
            }
        });
    }

    private int getNbChunk(byte[] result) {
        return (result.length / BUF_SIZE) + ((result.length % BUF_SIZE) > 0 ? 1 : 0);
    }

    private void sendChunk(final String address, final com.dcall.core.configuration.generic.entity.message.Message<String> sender, final byte[] bytes, final int nbChunk, final com.dcall.core.configuration.generic.entity.message.Message<String> resp) {
        for (int i = 0; i < nbChunk; i++) {
            final int startIdx = i * BUF_SIZE;
            final int idx = startIdx + BUF_SIZE;
            final int endIdx = (idx > bytes.length) ? bytes.length : idx;

            resp.setMessage(Arrays.copyOfRange(bytes, startIdx, endIdx)).setLength(endIdx - startIdx);

            vertx.eventBus().send(URIUtils.getUri(address, sender.getId()), Json.encodeToBuffer(resp), r -> {
                        if (r.succeeded())
                            LOG.info(r.result().body().toString());
                        else
                            new TechnicalException(r.cause()).log();
                    });
        }
    }

    private void handleError(final Message<Object> handler, final String msgError, final com.dcall.core.configuration.generic.entity.message.Message<String> sender) {
        final String error = "Failed to execute '" + new String(sender.getMessage()) + "' - ERROR : " + msgError;
        final byte[] bytes = error.getBytes();
        final String randId = HashProvider.seedSha512(bytes);
        final com.dcall.core.configuration.generic.entity.message.Message<String> resp = new MessageBean(randId, bytes, bytes.length);

        LOG.error(msgError);

        handler.fail(-1, "");

        sendChunk(URIConfig.URI_CLIENT_TERMINAL_CONSUMER, sender, bytes, getNbChunk(bytes), resp);
    }

    @Override
    public void start() {
        final MessageConsumer<Object> consumer = vertx.eventBus().consumer(LocalCommandProcessorConsumerVerticle.class.getName());

        consumer.handler(handler -> {
            final com.dcall.core.configuration.generic.entity.message.Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
            LOG.info(CommandProcessorConsumerVerticle.class.getSimpleName() + " > received from : " + msg.getId() + " body : " + handler.body().toString());
            execute(handler, msg);
        });
    }
}
