package com.dcall.core.app.client.vertx;

import com.dcall.core.configuration.exception.TechnicalException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class OutputConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(OutputConsumerVerticle.class);
    private final int BUF_SIZE = 2048;
    private final File output = new File("output");

    private void execute(final Message<Object> handler) {
        if (handler != null) {
            try {
                final ProcessBuilder processBuilder = new ProcessBuilder();

                final Process process = processBuilder.redirectErrorStream(true)
                        .command(handler.body().toString().split(" "))
                        .start();
                handleProcess(handler, process);
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    private void handleProcess(final Message<Object> handler, final Process process) {

        vertx.executeBlocking(future -> {
            final byte[] buffer = new byte[BUF_SIZE];
            int nread = 0;
            try {
                if ((nread = process.getInputStream().read(buffer, 0, BUF_SIZE)) > 0) {
                    vertx.eventBus().send(InputConsumerVerticle.class.getName(), new String(buffer).substring(0, nread - 1), r -> {
                        if (r.succeeded())
                            LOG.info(r.result().body().toString());
                        else
                            new TechnicalException(r.cause()).log();
                    });
                }
                future.complete();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }, res -> {
            if (res.succeeded()) {
                final boolean isAlive = process.isAlive();
                if (isAlive) {
                    handleProcess(handler, process);
                }
                else {
                    LOG.debug(" > reading successful (alive = " + isAlive + ")");
                    handler.reply(" > DONE");
                }
            }
            else
                LOG.debug(" > reading failed");
        });
    }

    @Override
    public void start() {
//        MessageConsumer<String> messageConsumer = vertx.eventBus().consumer(OutputConsumerVerticle.class.getSimpleName());
        vertx.eventBus().consumer(OutputConsumerVerticle.class.getSimpleName(), handler -> {
            LOG.info(OutputConsumerVerticle.class.getSimpleName() + " received : " + handler.body().toString());
            execute(handler);
        });
    }

}
