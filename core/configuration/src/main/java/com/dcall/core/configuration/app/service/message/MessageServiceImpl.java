package com.dcall.core.configuration.app.service.message;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.service.fingerprint.FingerPrintService;
import com.dcall.core.configuration.generic.vertx.cluster.HazelcastCluster;
import com.dcall.core.configuration.utils.SerializationUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class MessageServiceImpl implements MessageService {
    private static final Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);

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
}
