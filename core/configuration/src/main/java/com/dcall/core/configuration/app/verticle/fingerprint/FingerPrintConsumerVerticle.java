package com.dcall.core.configuration.app.verticle.fingerprint;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.app.security.rsa.RSAProvider;
import com.dcall.core.configuration.generic.vertx.cluster.HazelcastCluster;
import com.dcall.core.configuration.utils.SerializationUtils;
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
        final MessageConsumer<Object> consumer = vertx.eventBus().consumer(uriContext().getLocalConsumerUri());

        consumer.handler(handler -> {
            final com.dcall.core.configuration.app.entity.message.Message<String> msg = Json.decodeValue((Buffer) handler.body(), MessageBean.class);
            if (!msg.getId().equals(HazelcastCluster.getLocalUuid())) {
                final FingerPrint<String> fingerPrint = SerializationUtils.deserialize(msg.getMessage());
                LOG.info(uriContext.getLocalConsumerUri() + " > received public key from : " + msg.getId() + " body : " + RSAProvider.encodeKey(fingerPrint.getPublicKey()));
            }
        });
    }
}
