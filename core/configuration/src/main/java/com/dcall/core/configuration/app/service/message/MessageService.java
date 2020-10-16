package com.dcall.core.configuration.app.service.message;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.task.Task;
import com.dcall.core.configuration.app.exception.ExceptionHolder;
import com.dcall.core.configuration.app.exception.TechnicalException;
import com.dcall.core.configuration.generic.cluster.vertx.VertxCompletableFuture;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

public interface MessageService extends Serializable {
    MessageService send(final String id, final String address, final byte[] datas, final Consumer<AsyncResult<Message<Object>>> onSuccess, final Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback);
    MessageService send(final String address, final byte[] datas, final Consumer<AsyncResult<Message<Object>>> onSuccess, final Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback);
    <T> MessageService send(final String address, final T obj, final Consumer<AsyncResult<Message<Object>>> onSuccess, final Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback);
    MessageService publish(final String address, byte[] datas, final DeliveryOptions deliveryOptions);
    <T> MessageService publish(final String address, final T obj, final DeliveryOptions deliveryOptions);
    byte[] encryptMessage(final RuntimeContext runtimeContext, final FingerPrint fingerPrint, final byte[] datas);
    byte[] encryptMessage(final RuntimeContext runtimeContext, final com.dcall.core.configuration.app.entity.message.Message sender, final byte[] datas);
    byte[] decryptMessage(final RuntimeContext runtimeContext, final com.dcall.core.configuration.app.entity.message.Message sender);
    MessageService sendEncryptedChunk(final RuntimeContext runtimeContext, final Vertx vertx, final String address, com.dcall.core.configuration.app.entity.message.Message<String> sender, final byte[] bytes, final com.dcall.core.configuration.app.entity.message.Message<String> resp) throws Exception;
    MessageService sendEncryptedChunk(final RuntimeContext runtimeContext, final String address, byte[] bytes, final FingerPrint<String> fingerPrint) throws Exception;
    MessageService sendBlockingEncryptedChunk(final RuntimeContext runtimeContext, final Vertx vertx, final String address, final byte[] bytes, final int nbChunk, final int chunkIdx,
                                              final FingerPrint<String> fingerPrint, final com.dcall.core.configuration.app.entity.message.Message<String> msgTransporter);

    MessageService sendEncryptedChunk(final RuntimeContext runtimeContext, final Vertx vertx, final String address, final byte[] bytes, final int nbChunk, final int chunkIdx,
                                      final FingerPrint<String> fingerPrint, final com.dcall.core.configuration.app.entity.message.Message<String> msgTransporter, final Task task);

    // utils
    MessageService setBufSize(final int size);
    int getNbChunk(byte[] result);
}
