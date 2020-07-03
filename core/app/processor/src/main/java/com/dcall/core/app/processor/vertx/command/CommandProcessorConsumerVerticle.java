package com.dcall.core.app.processor.vertx.command;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import com.dcall.core.configuration.app.exception.TechnicalException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class CommandProcessorConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(CommandProcessorConsumerVerticle.class);
    @Autowired private RuntimeContext runtimeContext;
    private VertxURIContext uriContext;
    private final int BUF_SIZE = 2048;
    private InputStreamReader inputStreamReader;

    private void execute(final Message<Object> handler) {
        if (handler != null) {
            try {
                final ProcessBuilder processBuilder = new ProcessBuilder();

                final Process process = processBuilder.redirectErrorStream(true)
                        .command(handler.body().toString().split(" "))
                        .start();
                inputStreamReader = new InputStreamReader(process.getInputStream(), "UTF-8");
                handleProcess(handler);
            } catch (IOException e) {
                handleError(handler, e.getMessage());
            }
        }
    }

    private void handleError(final Message<Object> handler, final String msgError) {
        LOG.error(msgError);
        handler.fail(-1, "");
        vertx.eventBus().send(uriContext.getRemoteConsumerUri(), msgError, r -> {
            if (r.succeeded())
                LOG.info(r.result().body().toString());
            else
                new TechnicalException(r.cause()).log();
        });
    }

    private void handleProcess(final Message<Object> handler) {
        vertx.executeBlocking(future -> {
            final char[] buffer = new char[BUF_SIZE];

            try {
                int nread;
                while ((nread = inputStreamReader.read(buffer, 0, BUF_SIZE)) > 0) {
                    vertx.eventBus().send(uriContext.getRemoteConsumerUri(), new String(buffer).substring(0, nread), r -> {
                        if (r.succeeded())
                            LOG.info(r.result().body().toString());
                        else
                            new TechnicalException(r.cause()).log();
                    });
                }
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
            future.complete();
        }, res -> {
            if (res.succeeded()) {
                try {
                    inputStreamReader.close();
                    handler.reply(" > DONE");
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            } else {
                LOG.debug(" > reading failed");
                handler.reply(res.cause());
            }
        });
    }

    private void configureURI() {
        uriContext = this.runtimeContext.systemContext().routeContext().getVertxContext().getVertxURIContext();

        uriContext.setBaseRemoteAppUri(uriContext.getLocalUri("terminal.vertx"));

        uriContext.setLocalConsumerUri(CommandProcessorConsumerVerticle.class.getName());
        uriContext.setRemoteConsumerUri(uriContext.getRemoteUri("InputConsumerVerticle"));
    }

    @Override
    public void start() {
        configureURI();
        final MessageConsumer<Object> consumer = vertx.eventBus()
                .consumer(uriContext.getLocalConsumerUri());

        consumer.handler(handler -> {
            LOG.info(uriContext.getLocalConsumerUri() + " > received : "
                    + handler.body().toString());
            execute(handler);
        });
    }
}
