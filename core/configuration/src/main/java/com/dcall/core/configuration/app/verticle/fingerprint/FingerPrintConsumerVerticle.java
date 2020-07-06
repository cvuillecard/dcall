package com.dcall.core.configuration.app.verticle.fingerprint;

import com.dcall.core.configuration.app.constant.ClusterConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.fingerprint.FingerPrintContext;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import com.dcall.core.configuration.app.entity.cipher.CipherAES;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.security.rsa.RSAProvider;
import com.dcall.core.configuration.app.service.cipher.CipherService;
import com.dcall.core.configuration.generic.vertx.cluster.HazelcastCluster;
import com.dcall.core.configuration.utils.SerializationUtils;
import com.dcall.core.configuration.utils.URIUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class FingerPrintConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(FingerPrintConsumerVerticle.class);
    @Autowired private RuntimeContext runtimeContext;
    private VertxURIContext uriContext = new VertxURIContext();

    private VertxURIContext uriContext() {
        return uriContext.setBaseLocalAppUri(this.getClass().getPackage().getName())
                .setBaseRemoteAppUri(uriContext.getBaseLocalAppUri())
                .setLocalConsumerUri(this.getClass().getName())
                .setRemoteConsumerUri(uriContext.getLocalConsumerUri());
    }

    @Override
    public void start() {
        final FingerPrintContext fingerPrintContext = runtimeContext.clusterContext().fingerPrintContext();

        final MessageConsumer<Object> publicConsumer = vertx.eventBus().consumer(uriContext().getLocalConsumerUri());
        final MessageConsumer<Object> privateConsumer = vertx.eventBus().consumer(URIUtils.getUri(uriContext().getLocalConsumerUri(), HazelcastCluster.getLocalUuid()));

        publicConsumer.handler(handler -> handleMessage(fingerPrintContext, (Buffer) handler.body(), true));
        privateConsumer.handler(handler -> handleMessage(fingerPrintContext, (Buffer) handler.body(), false));

    }

    private void handleMessage(final FingerPrintContext fingerPrintContext, final Buffer buffer, final boolean isPublic) {
        try {
            final CipherService cipherService = runtimeContext.serviceContext().serviceProvider().hashServiceProvider().cipherService();
            final com.dcall.core.configuration.app.entity.message.Message<String> msg = Json.decodeValue(buffer, MessageBean.class);

            if (!msg.getId().equals(HazelcastCluster.getLocalUuid())) {
                FingerPrint<String> fingerPrint = SerializationUtils.deserialize(isPublic ? msg.getMessage()
                        : RSAProvider.decrypt(msg.getMessage(), runtimeContext.userContext().getCertificate().getKeyPair().getPrivate()));
                if (fingerPrintContext.getFingerprints().get(msg.getId()) == null) {
                    fingerPrintContext.getFingerprints().put(msg.getId(), fingerPrint);
                    LOG.info(" > received public key from : " + msg.getId() + " < [ public_id :" + fingerPrint.getId() + " ] + [ public key : " + RSAProvider.encodeKey(fingerPrint.getPublicKey()) + " ]");

                    runtimeContext.serviceContext().serviceProvider().messageServiceProvider().fingerPrintService().sendCipherTransporter(runtimeContext, fingerPrint);
                } else {
                    fingerPrintContext.getFingerprints().get(msg.getId()).setCipherAES(fingerPrint.getCipherAES());
                }
            }
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
        }
    }
}
