package com.dcall.core.configuration.app.verticle.fingerprint;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.fingerprint.FingerPrintContext;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrintBean;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.app.security.rsa.RSAProvider;
import com.dcall.core.configuration.app.service.fingerprint.FingerPrintService;
import com.dcall.core.configuration.generic.vertx.cluster.HazelcastCluster;
import com.dcall.core.configuration.generic.vertx.uri.VertxURIConfig;
import com.dcall.core.configuration.utils.SerializationUtils;
import com.dcall.core.configuration.utils.URIUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
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
        final String privateUri = URIUtils.getUri(uriContext().getLocalConsumerUri(), HazelcastCluster.getLocalUuid());
        final MessageConsumer<Object> publicConsumer = vertx.eventBus().consumer(uriContext().getLocalConsumerUri());
        final MessageConsumer<Object> privateConsumer = vertx.eventBus().consumer(privateUri);
        final MessageConsumer<Object> idConsumer = vertx.eventBus().consumer(URIUtils.getUri(privateUri, VertxURIConfig.ID_DOMAIN));

        publicConsumer.handler(handler -> handlePublicMessage(fingerPrintContext, handler));
        privateConsumer.handler(handler -> handlePrivateMessage(fingerPrintContext, handler));
        idConsumer.handler(handler -> handleIdMessage(fingerPrintContext, handler));
    }

    private void handleIdMessage(final FingerPrintContext fingerPrintContext, final Message<Object> handler) {
        vertx.executeBlocking(future -> {
            try {
                final com.dcall.core.configuration.app.entity.message.Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);

                final FingerPrint fingerPrint = fingerPrintContext.getFingerprints().get(msg.getId());

                if (!msg.getId().equals(HazelcastCluster.getLocalUuid()) && fingerPrint != null && fingerPrint.getId().equals(msg.getId())) {
                    final byte[] bytes = RSAProvider.decrypt(msg.getMessage(), runtimeContext.userContext().getCertificate().getKeyPair().getPrivate());
                    fingerPrint.setId(new String(bytes));
                }
            }
            catch (Exception e) {
                future.fail(e.getMessage());
                LOG.error(e.getMessage());
            }
        }, null);
    }

    private void handlePrivateMessage(final FingerPrintContext fingerPrintContext, final Message<Object> handler) {
        vertx.executeBlocking(future -> {
            try {
                final FingerPrintService fingerPrintService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().fingerPrintService();
                final com.dcall.core.configuration.app.entity.message.Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
                final FingerPrint<String> fromFingerPrint = fingerPrintContext.getFingerprints().get(msg.getId());

                if (!msg.getId().equals(HazelcastCluster.getLocalUuid()) &&  fromFingerPrint != null) {
                    final byte[] bytes = RSAProvider.decrypt(msg.getMessage(), runtimeContext.userContext().getCertificate().getKeyPair().getPrivate());
                    fromFingerPrint.setSecretKey(SerializationUtils.deserialize(bytes));
                    fingerPrintService.sendPublicId(runtimeContext, fromFingerPrint, msg);
                } else
                    fingerPrintContext.getFingerprints().put(msg.getId(), SerializationUtils.deserialize(msg.getMessage()));
                future.complete();

            } catch (Exception e) {
                future.fail(e.getMessage());
                LOG.error(e.getMessage());
            }
        }, null);
    }

    private void handlePublicMessage(final FingerPrintContext fingerPrintContext, final Message<Object> handler) {
        vertx.executeBlocking(future -> {
            try {
                final com.dcall.core.configuration.app.entity.message.Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);

                if (!msg.getId().equals(HazelcastCluster.getLocalUuid())) {
                    FingerPrint<String> fingerPrint = SerializationUtils.deserialize(msg.getMessage());
                    if (fingerPrintContext.getFingerprints().get(msg.getId()) == null) {
                        fingerPrintContext.getFingerprints().put(msg.getId(), fingerPrint);
                        LOG.info(" > received public key from : " + msg.getId() + " < [ public_id :" + fingerPrint.getId() + " ] + [ public key : " + RSAProvider.encodeKey(fingerPrint.getPublicKey()) + " ]");
                    runtimeContext.serviceContext().serviceProvider().messageServiceProvider().fingerPrintService().sendPublicUserCertificate(runtimeContext, msg);
                    runtimeContext.serviceContext().serviceProvider().messageServiceProvider().fingerPrintService().sendSecretKey(runtimeContext, fingerPrint, msg);
                    }
                }
                future.complete();
            }
            catch (Exception e) {
                future.fail(e.getMessage());
                LOG.error(e.getMessage());
            }
        }, null);
    }
}
