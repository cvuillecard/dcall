package com.dcall.core.configuration.app.service.fingerprint;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrintBean;
import com.dcall.core.configuration.app.entity.message.Message;
import com.dcall.core.configuration.app.security.aes.AESProvider;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.security.rsa.RSAProvider;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.verticle.fingerprint.FingerPrintConsumerVerticle;
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

        fingerPrint.setId(userContext.getEnviron().getProperties().getProperty(EnvironConstant.PUBLIC_ID));
        fingerPrint.setPublicKey(userContext.getCertificate().getKeyPair().getPublic());

        messageService.publish(FingerPrintConsumerVerticle.class.getName(), fingerPrint, null);

        return this;
    }

    @Override
    public FingerPrintService sendPublicUserCertificate(final RuntimeContext runtimeContext, final Message<String> fromMessage) {
        final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().getMessageService();
        final FingerPrint<String> fingerPrint = new FingerPrintBean();

        fingerPrint.setId(runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.PUBLIC_ID));
        fingerPrint.setPublicKey(runtimeContext.userContext().getCertificate().getKeyPair().getPublic());

        messageService.send(
                runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.PUBLIC_ID),
                URIUtils.getUri(FingerPrintConsumerVerticle.class.getName(), fromMessage.getId()),
                SerializationUtils.serialize(fingerPrint),
                success -> LOG.debug("> SUCCESS : public certificate sent to [" + fromMessage.getId() + ']'),
                failed -> LOG.debug("> FAILED TO SEND PUBLIC CERTIFICATE TO [" + fromMessage.getId() + ']'),
                null);

        return this;
    }

    @Override
    public FingerPrintService sendCipherTransporter(final RuntimeContext runtimeContext, final FingerPrint<String> fromFingerPrint, final Message<String> fromMessage) {
        try {
            if (fromFingerPrint.getSecretKey() == null) {
                final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().getMessageService();

                final String p = HashProvider.signSha512(runtimeContext.userContext().getUserHash().getSalt());
                final String s = HashProvider.createSalt512(fromFingerPrint.getId(), RSAProvider.encodeKey(fromFingerPrint.getPublicKey()));
                final SecretKey secretKey = AESProvider.getSecretKey(p, s.getBytes());

                fromFingerPrint.setSecretKey(secretKey);

                final byte[] bytes = RSAProvider.encrypt(SerializationUtils.serialize(secretKey), fromFingerPrint.getPublicKey());

                messageService.send(
                        runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.PUBLIC_ID),
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
}
