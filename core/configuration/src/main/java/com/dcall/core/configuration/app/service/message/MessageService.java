package com.dcall.core.configuration.app.service.message;

import com.dcall.core.configuration.app.context.RuntimeContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

import java.util.function.Consumer;

public interface MessageService {
    MessageService send(final String id, final String address, final byte[] datas, final Consumer<AsyncResult<Message<Object>>> onSuccess, final Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback);
    MessageService send(final String address, final byte[] datas, final Consumer<AsyncResult<Message<Object>>> onSuccess, final Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback);
    <T> MessageService send(final String address, final T obj, final Consumer<AsyncResult<Message<Object>>> onSuccess, final Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback);
    MessageService publish(final String address, byte[] datas, final DeliveryOptions deliveryOptions);
    <T> MessageService publish(final String address, final T obj, final DeliveryOptions deliveryOptions);
    byte[] decryptMessage(final RuntimeContext runtimeContext, final com.dcall.core.configuration.app.entity.message.Message sender);
    byte[] encryptMessage(final RuntimeContext runtimeContext, final com.dcall.core.configuration.app.entity.message.Message sender, final byte[] datas);
}
