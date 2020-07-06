package com.dcall.core.configuration.app.service.fingerprint;

import com.dcall.core.configuration.app.constant.ClusterConstant;
import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.entity.cipher.CipherAES;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrintBean;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.security.rsa.RSAProvider;
import com.dcall.core.configuration.app.service.cipher.CipherService;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.verticle.fingerprint.FingerPrintConsumerVerticle;
import com.dcall.core.configuration.utils.SerializationUtils;
import com.dcall.core.configuration.utils.URIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public FingerPrintService sendCipherTransporter(final RuntimeContext runtimeContext, final FingerPrint<String> fromFingerPrint) {
        try {
            if (fromFingerPrint.getCipherAES() == null) {
                final CipherService cipherService = runtimeContext.serviceContext().serviceProvider().hashServiceProvider().cipherService();
                final MessageService messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().getMessageService();
                final FingerPrint<String> toFingerPrint = new FingerPrintBean();

                fromFingerPrint.setId(runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.PUBLIC_ID));
                fromFingerPrint.setPublicKey(runtimeContext.userContext().getCertificate().getKeyPair().getPublic());

                final CipherAES<String> cipher = cipherService.createCipherAES(
                        runtimeContext.userContext().getUserHash().getSalt(),
                        HashProvider.createSalt512(fromFingerPrint.getId(), RSAProvider.encodeKey(fromFingerPrint.getPublicKey())),
                        ClusterConstant.PRIV_PWD, null);
                
                fromFingerPrint.setCipherAES(cipher);
                toFingerPrint.setCipherAES(cipher);

                final byte[] bytes = RSAProvider.encrypt(SerializationUtils.serialize(toFingerPrint), fromFingerPrint.getPublicKey());

                messageService.send(
                        URIUtils.getUri(FingerPrintConsumerVerticle.class.getName(), fromFingerPrint.getId()),
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
