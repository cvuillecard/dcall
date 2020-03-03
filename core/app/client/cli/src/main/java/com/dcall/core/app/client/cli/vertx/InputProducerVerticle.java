package com.dcall.core.app.client.cli.vertx;

import com.dcall.core.configuration.vertx.VertxApplication;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class InputProducerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(InputProducerVerticle.class);
    private String input;
    private boolean exit = false;

    private boolean readSTDIN() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        if (!br.ready()) {
            return false;
        }

        final String input = br.readLine().trim().toLowerCase();

        if (input != null && !(exit = input.equals("exit"))) {
            LOG.info("input > " + input);
            final MessageProducer<String> producer = vertx.eventBus().sender(OutputConsumerVerticle.class.getSimpleName());
            producer.send(input, handler -> {
                if (handler.succeeded()) {
                    LOG.info(handler.result().body().toString());
                }
            });
        }
        else
            VertxApplication.shutdown();

        return exit;
    }

    @Override
    public void start() {
        handler();
    }

    private void handler() {
        vertx.executeBlocking(future -> {
            try {
                future.complete(readSTDIN());
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }, res -> {
            if (res.succeeded()) {
//                LOG.info("execute blocking code res succeeded");
                if (res.result().equals(true))
                    LOG.info("=> exit asked");
                else
                    handler();
            }
        });
    }
}
