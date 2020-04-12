package com.dcall.core.app.processor.vertx.command.local;

import com.dcall.core.app.processor.bean.local.controller.command.LocalCommandController;
import com.dcall.core.app.processor.vertx.command.CommandProcessorConsumerVerticle;
import com.dcall.core.app.processor.vertx.constant.URIConfig;
import com.dcall.core.configuration.entity.MessageBean;
import com.dcall.core.configuration.exception.TechnicalException;
import com.dcall.core.configuration.security.hash.HashProvider;
import com.dcall.core.configuration.utils.URIUtils;
import com.dcall.core.configuration.vertx.cluster.HazelcastCluster;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class LocalCommandProcessorConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(LocalCommandProcessorConsumerVerticle.class);

    private final int BUF_SIZE = 8192;
    private final LocalCommandController commandController = new LocalCommandController();

    private void execute(final Message<Object> handler, final com.dcall.core.configuration.bo.Message<String> msg) {
        if (handler != null) {
            try {
                handleLocalCommand(handler, msg);
            } catch (Exception e) {
                handleError(handler, e.getMessage(), msg);
            }
        }
    }

    private void handleLocalCommand(final Message<Object> handler, final com.dcall.core.configuration.bo.Message<String> msg) {
        vertx.executeBlocking(future -> {
            try {
                final byte[] result = commandController.execute(new String(msg.getMessage()));
                final int nbReq = result.length / BUF_SIZE;
                final int rest = result.length % BUF_SIZE;
                final int totalReq = nbReq + (rest > 0 ? 1 : 0);

                final com.dcall.core.configuration.bo.Message<String> resp = new MessageBean(HazelcastCluster.getLocalUuid(), null, 0);

                for (int i = 0; i < totalReq; i++) {
                    final int startIdx = i * BUF_SIZE;
                    final int idx = startIdx + BUF_SIZE;
                    final int endIdx = (idx > result.length) ? result.length : idx;
                    resp.setMessage(Arrays.copyOfRange(result, startIdx, endIdx)).setLength(endIdx - startIdx);
                    vertx.eventBus().send(URIUtils.getUri(URIConfig.URI_CLIENT_TERMINAL_CONSUMER, msg.getId()), Json.encodeToBuffer(resp), r -> {
                                if (r.succeeded())
                                    LOG.info(r.result().body().toString());
                                else
                                    new TechnicalException(r.cause()).log();
                            });
                }
            }
            catch (Exception e) {
                handleError(handler, e.getMessage(), msg);
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

    private void handleError(final Message<Object> handler, final String msgError, final com.dcall.core.configuration.bo.Message<String> msg) {
        LOG.error(msgError);
        handler.fail(-1, "");
        final byte[] bytes = msgError.getBytes();
        final String randId = HashProvider.seed(bytes);
        final Buffer buffer = Json.encodeToBuffer(new MessageBean(randId, bytes, bytes.length));
        vertx.eventBus().send(URIUtils.getUri(URIConfig.URI_CLIENT_TERMINAL_CONSUMER, msg.getId()), buffer, r -> {
            if (r.succeeded())
                LOG.info(r.result().body().toString());
            else
                new TechnicalException(r.cause()).log();
        });
    }

    @Override
    public void start() {
        final MessageConsumer<Object> consumer = vertx.eventBus().consumer(LocalCommandProcessorConsumerVerticle.class.getName());

        consumer.handler(handler -> {
            final com.dcall.core.configuration.bo.Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
            LOG.info(CommandProcessorConsumerVerticle.class.getSimpleName() + " > received from : " + msg.getId() + " body : " + handler.body().toString());
            execute(handler, msg);
        });
    }
}
