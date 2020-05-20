package com.dcall.core.configuration.service.message;

import com.dcall.core.configuration.entity.message.MessageBean;
import com.dcall.core.configuration.vertx.cluster.HazelcastCluster;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class MessageServiceImpl implements MessageService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Override
    public void sendInputMessage(final String address, final byte[] datas, final Consumer<AsyncResult<Message<Object>>> onSuccess, Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback) {
        final com.dcall.core.configuration.entity.message.Message<String> msg = new MessageBean(HazelcastCluster.getLocalUuid(), datas, datas.length);

        Vertx.currentContext().owner().eventBus()
                .send(address, Json.encodeToBuffer(msg), res -> {
                    if (res.succeeded()) {
                        if (onSuccess != null)
                            onSuccess.accept(res);
                        LOG.debug(" > URI : " + address + " replied (SUCCESS) : " + res.result());
                    } else {
                        if (onFail != null)
                            onFail.accept(res);
                        LOG.error(" > URI : " + address + " replied (FAILED) : " + res.cause().getMessage());
                    }
                    if (callback != null) {
                        callback.run();
                        LOG.debug(" > URI : " + address + " callback executed");
                    }
                });
    }
}
