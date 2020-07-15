package com.dcall.core.configuration.app.service.message;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.app.exception.TechnicalException;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.service.fingerprint.FingerPrintService;
import com.dcall.core.configuration.generic.cluster.hazelcast.HazelcastCluster;
import com.dcall.core.configuration.utils.SerializationUtils;
import com.dcall.core.configuration.utils.URIUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

public class MessageServiceImpl implements MessageService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);
    private int BUF_SIZE = 8192;

    @Override
    public MessageService send(final String id, final String address, final byte[] datas, final Consumer<AsyncResult<Message<Object>>> onSuccess, Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback) {
        final com.dcall.core.configuration.app.entity.message.Message<String> msg = new MessageBean(id, datas, datas.length);

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

        return this;
    }

    @Override
    public MessageService send(final String address, final byte[] datas, final Consumer<AsyncResult<Message<Object>>> onSuccess, Consumer<AsyncResult<Message<Object>>> onFail, final Runnable callback) {
        send(HazelcastCluster.getLocalUuid(), address, datas, onSuccess, onFail, callback);
        return this;
    }

    @Override
    public <T> MessageService send(String address, T obj, Consumer<AsyncResult<Message<Object>>> onSuccess, Consumer<AsyncResult<Message<Object>>> onFail, Runnable callback) {
        return send(address, SerializationUtils.serialize(obj), onSuccess, onFail, callback);
    }

    @Override
    public MessageService publish(final String address, final byte[] datas, final DeliveryOptions deliveryOptions) {
        final com.dcall.core.configuration.app.entity.message.Message<String> msg = new MessageBean(HazelcastCluster.getLocalUuid(), datas, datas.length);

        Vertx.currentContext().owner().eventBus().publish(address, Json.encodeToBuffer(msg), deliveryOptions != null ? deliveryOptions : new DeliveryOptions());

        LOG.debug(" > publish message [ id = " + msg.getId() + "] [ datas = ", datas.toString(), " ]");

        return this;
    }

    @Override
    public <T> MessageService publish(final String address, final T obj, final DeliveryOptions deliveryOptions) {
        return publish(address, SerializationUtils.serialize(obj), deliveryOptions);
    }

    @Override
    public byte[] encryptMessage(final RuntimeContext runtimeContext, final FingerPrint fingerPrint, final byte[] datas) {
        try {
            final FingerPrintService fingerPrintService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().fingerPrintService();
            final AbstractCipherResource cipherResource = (AbstractCipherResource) fingerPrint;

            if (cipherResource.getCipher() == null)
                fingerPrintService.updateCipherFingerPrint((FingerPrint) cipherResource);

            return AESProvider.encryptBytes(datas, cipherResource.getCipher().getCipherIn());
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public byte[] encryptMessage(final RuntimeContext runtimeContext, final com.dcall.core.configuration.app.entity.message.Message sender, final byte[] datas) {
        return encryptMessage(runtimeContext, runtimeContext.clusterContext().fingerPrintContext().getFingerprints().get(sender.getId()), datas);
    }

    @Override
    public byte[] decryptMessage(final RuntimeContext runtimeContext, final com.dcall.core.configuration.app.entity.message.Message sender) {
        try {
            final FingerPrintService fingerPrintService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().fingerPrintService();
            final AbstractCipherResource cipherResource = (AbstractCipherResource) runtimeContext.clusterContext().fingerPrintContext().getFingerprints().get(sender.getId());

            if (cipherResource.getCipher() == null)
                fingerPrintService.updateCipherFingerPrint((FingerPrint) cipherResource);

            return AESProvider.decryptBytes(sender.getMessage(), cipherResource.getCipher().getCipherOut());
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public void sendEncryptedChunk(final RuntimeContext runtimeContext, final Vertx vertx, final String address, final com.dcall.core.configuration.app.entity.message.Message<String> sender, final byte[] bytes, final com.dcall.core.configuration.app.entity.message.Message<String> resp) {
        final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService();
        final int nbChunk = getNbChunk(bytes);

        for (int i = 0; i < nbChunk; i++) {
            final int startIdx = i * BUF_SIZE;
            final int nextIdx = startIdx + BUF_SIZE;
            final int endIdx = (nextIdx > bytes.length) ? bytes.length : nextIdx;

            final byte[] datas = messageService.encryptMessage(runtimeContext, sender, Arrays.copyOfRange(bytes, startIdx, endIdx));

            resp.setMessage(datas).setLength(datas.length);

            vertx.eventBus().send(URIUtils.getUri(address, sender.getId()), Json.encodeToBuffer(resp), r -> {
                if (r.succeeded())
                    LOG.info(r.result().body().toString());
                else
                    new TechnicalException(r.cause()).log();
            });
        }
    }

    @Override
    public void sendEncryptedChunk(final RuntimeContext runtimeContext, final String address, final byte[] bytes, final FingerPrint<String> fingerPrint) {
        final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService();
        final com.dcall.core.configuration.app.entity.message.Message<String> msg = new MessageBean().setId(HazelcastCluster.getLocalUuid());
        final int nbChunk = getNbChunk(bytes);

        for (int i = 0; i < nbChunk; i++) {
            final int startIdx = i * BUF_SIZE;
            final int nextIdx = startIdx + BUF_SIZE;
            final int endIdx = (nextIdx > bytes.length) ? bytes.length : nextIdx;

            final byte[] datas = messageService.encryptMessage(runtimeContext, fingerPrint, Arrays.copyOfRange(bytes, startIdx, endIdx));

            msg.setMessage(datas).setLength(datas.length);

            Vertx.currentContext().owner().eventBus().send(address, Json.encodeToBuffer(msg), r -> {
                if (r.succeeded())
                    LOG.info(r.result().body().toString());
                else
                    new TechnicalException(r.cause()).log();
            });
        }
    }

    @Override
    public MessageService setBufSize(final int size) { this.BUF_SIZE = size; return this; }

    private int getNbChunk(final byte[] result) {
        return (result.length / BUF_SIZE) + ((result.length % BUF_SIZE) > 0 ? 1 : 0);
    }
}
