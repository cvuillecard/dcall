package com.dcall.core.app.client.cli.vertx;

import com.dcall.core.configuration.exception.TechnicalException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
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
    private final File file = new File("output");
    private InputStreamReader inputStreamReader;

    private void execute(final Message<Object> handler) {
        if (handler != null) {
            try {
                final ProcessBuilder processBuilder = new ProcessBuilder();

                final Process process = processBuilder.redirectErrorStream(true)
                        .command(handler.body().toString().split(" "))
                        .start();
                inputStreamReader = new InputStreamReader(process.getInputStream());
                handleProcess(handler, process);
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    private void handleProcess(final Message<Object> handler, final Process process) {
        vertx.executeBlocking(future -> {
            final char[] buffer = new char[BUF_SIZE];

            try {
                int nread;
                while ((nread = inputStreamReader.read(buffer, 0, BUF_SIZE)) > 0) {
                    vertx.eventBus().send(InputConsumerVerticle.class.getName(), new String(buffer).substring(0, nread), r -> {
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
            } else
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
