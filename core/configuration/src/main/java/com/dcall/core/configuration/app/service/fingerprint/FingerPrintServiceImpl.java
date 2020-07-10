package com.dcall.core.configuration.app.service.fingerprint;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.fingerprint.FingerPrintContext;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.app.entity.cipher.CipherAESBean;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrintBean;
import com.dcall.core.configuration.app.entity.message.Message;
import com.dcall.core.configuration.app.exception.FunctionalException;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.security.rsa.RSAProvider;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.verticle.fingerprint.FingerPrintConsumerVerticle;
import com.dcall.core.configuration.generic.cluster.hazelcast.HazelcastCluster;
import com.dcall.core.configuration.generic.cluster.vertx.uri.VertxURIConfig;
import com.dcall.core.configuration.utils.SerializationUtils;
import com.dcall.core.configuration.utils.URIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;

public class FingerPrintServiceImpl implements FingerPrintService {
    private static final Logger LOG = LoggerFactory.getLogger(FingerPrintServiceImpl.class);
    private final MessageService messageService;

    public FingerPrintServiceImpl(final MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public FingerPrintService publishPublicUserCertificate(final UserContext userContext) {
        final FingerPrint<String> fingerPrint = new FingerPrintBean();

        fingerPrint.setId(HazelcastCluster.getLocalUuid());
        fingerPrint.setPublicKey(userContext.getCertificate().getKeyPair().getPublic());

        messageService.publish(FingerPrintConsumerVerticle.class.getName(), fingerPrint, null);
        return this;
    }

    @Override
    public FingerPrintService sendPublicUserCertificate(final RuntimeContext runtimeContext, final Message<String> fromMessage) {
        final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService();
        final FingerPrint<String> fingerPrint = new FingerPrintBean();

        fingerPrint.setId(HazelcastCluster.getLocalUuid());
        fingerPrint.setPublicKey(runtimeContext.userContext().getCertificate().getKeyPair().getPublic());

        messageService.send(
                URIUtils.getUri(FingerPrintConsumerVerticle.class.getName(), fromMessage.getId()),
                SerializationUtils.serialize(fingerPrint),
                success -> LOG.debug("> SUCCESS : public certificate sent to [" + fromMessage.getId() + ']'),
                failed -> LOG.debug("> FAILED TO SEND PUBLIC CERTIFICATE TO [" + fromMessage.getId() + ']'),
                null);

        return this;
    }

    @Override
    public FingerPrintService sendSecretKey(final RuntimeContext runtimeContext, final FingerPrint<String> fromFingerPrint, final Message<String> fromMessage) {
        try {
            if (fromFingerPrint.getSecretKey() == null) {
                final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService();

                final String p = HashProvider.signSha512(runtimeContext.userContext().getUserHash().getSalt());
                final String s = HashProvider.createSalt512(RSAProvider.encodeKey(fromFingerPrint.getPublicKey()), new String(HashProvider.random()));
                final SecretKey secretKey = AESProvider.getSecretKey(p, s.getBytes());

                fromFingerPrint.setSecretKey(secretKey);

                final byte[] bytes = RSAProvider.encrypt(SerializationUtils.serialize(secretKey), fromFingerPrint.getPublicKey());

                messageService.send(
                        URIUtils.getUri(FingerPrintConsumerVerticle.class.getName(), fromMessage.getId()),
                        bytes,
                        success -> LOG.debug("> SUCCESS : AES cipher transporter sent to [" + fromFingerPrint.getId() + ']'),
                        failed -> LOG.debug("> FAILED TO CREATE AES CIPHER TRANSPORTER FOR [" + fromFingerPrint.getId() + ']'),
                        null);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return this;
    }

    @Override
    public FingerPrintService sendPublicId(final RuntimeContext runtimeContext, final FingerPrint<String> fromFingerPrint, final Message<String> fromMessage) {
        try {
            if (fromFingerPrint.getSecretKey() != null) {
                final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService();
                final String publicId = runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.PUBLIC_ID);
                final byte[] bytes = RSAProvider.encrypt(publicId.getBytes(), fromFingerPrint.getPublicKey());
                final String uri = URIUtils.getUri(URIUtils.getUri(FingerPrintConsumerVerticle.class.getName(), fromMessage.getId()), VertxURIConfig.ID_DOMAIN);

                messageService.send(
                        uri,
                        bytes,
                        success -> LOG.debug("> SUCCESS : Public id sent to [" + fromFingerPrint.getId() + ']'),
                        failed -> LOG.debug("> FAILED TO SEND PUBLIC ID to [" + fromFingerPrint.getId() + ']'),
                        null);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return this;
    }

    @Override
    public FingerPrint updateCipherFingerPrint(final FingerPrint<String> fingerPrint) {
        if (fingerPrint != null && fingerPrint.getSecretKey() != null)
            ((AbstractCipherResource)fingerPrint).setCipher(new CipherAESBean(fingerPrint.getSecretKey()));

        return fingerPrint;
    }

    @Override
    public FingerPrint nextFingerPrint(final FingerPrintContext fingerPrintContext) throws FunctionalException {
        if (fingerPrintContext.getFingerprints().size() > 0)
            return fingerPrintContext.current() != null ? fingerPrintContext.current() : fingerPrintContext.next(fingerPrintContext.iterator());
        throw new FunctionalException("No fingerprints available in cache or no peers connected : perhaps you might use publish cmd to get a secure connection");
    }
}
