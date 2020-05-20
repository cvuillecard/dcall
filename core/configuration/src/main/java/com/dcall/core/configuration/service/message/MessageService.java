package com.dcall.core.configuration.service.message;

import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;

import java.util.function.Consumer;

public interface MessageService {
    void sendInputMessage(final String address, final byte[] datas, final Consumer<AsyncResult<Message<Object>>> onSuccess, final Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback);
}
