package com.dcall.core.app.client.vertx;

import com.hazelcast.internal.util.concurrent.Pipe;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.FileSystem;
import io.vertx.core.file.OpenOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class OutputConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(OutputConsumerVerticle.class);

    @Override
    public void start() {
//        MessageConsumer<String> messageConsumer = vertx.eventBus().consumer(OutputConsumerVerticle.class.getSimpleName());
        vertx.eventBus().consumer(OutputConsumerVerticle.class.getSimpleName(), handler -> {
//            FileSystem fs = vertx.fileSystem();
//            fs.open("pipe", new OpenOptions(), ar -> {
//                if (ar.succeeded()) {
//                    AsyncFile file = ar.result();
//                }
//            });
            executeHandler(handler.body().toString());
            LOG.info(OutputConsumerVerticle.class.getSimpleName() + " received : " + handler.body().toString());
//            ProcessBuilder processBuilder
            handler.reply("done");
        });
    }

    private void executeHandler(final String input) {
        if (input != null) {
            ProcessBuilder processBuilder = new ProcessBuilder();
        }
    }
}
