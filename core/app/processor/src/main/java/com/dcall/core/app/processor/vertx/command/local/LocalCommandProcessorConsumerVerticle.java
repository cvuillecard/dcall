package com.dcall.core.app.processor.vertx.command.local;

import com.dcall.core.app.processor.bean.local.controller.command.LocalCommandController;
import com.dcall.core.app.processor.vertx.command.CommandProcessorConsumerVerticle;
import com.dcall.core.app.processor.vertx.constant.URIConfig;
import com.dcall.core.configuration.exception.TechnicalException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
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
    private static final Logger LOG = LoggerFactory.getLogger(LocalCommandProcessorConsumerVerticle.class);

    private final int BUF_SIZE = 8192;
    @Autowired LocalCommandController commandController = new LocalCommandController();

    private void execute(final Message<Object> handler) {
        if (handler != null) {
            try {
                handleLocalCommand(handler);
            } catch (Exception e) {
                handleError(handler, e.getMessage());
            }
        }
    }

    private void handleLocalCommand(final Message<Object> handler) {
        vertx.executeBlocking(future -> {
            try {
                final byte[] result = commandController.execute(handler.body().toString());
                final int nbReq = result.length / BUF_SIZE;
                final int rest = result.length % BUF_SIZE;
                final int totalReq = nbReq + (rest > 0 ? 1 : 0);

                for (int i = 0; i < totalReq; i++) {
                    final int startIdx = i * BUF_SIZE;
                    final int endIdx = startIdx + BUF_SIZE;
                    vertx.eventBus().send(URIConfig.URI_CLIENT_TERMINAL_CONSUMER,
                            new String(Arrays.copyOfRange(result, startIdx, endIdx > result.length ? result.length : endIdx)), r -> {
                                if (r.succeeded())
                                    LOG.info(r.result().body().toString());
                                else
                                    new TechnicalException(r.cause()).log();
                            });
                }
            }
            catch (Exception e) {
                handleError(handler, e.getMessage());
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

    private void handleError(final Message<Object> handler, final String msgError) {
        LOG.error(msgError);
        handler.fail(-1, "");
        vertx.eventBus().send(URIConfig.URI_CLIENT_TERMINAL_CONSUMER, msgError, r -> {
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
            LOG.info(CommandProcessorConsumerVerticle.class.getSimpleName() + " > received : " + handler.body().toString());
            execute(handler);
        });
    }
}
