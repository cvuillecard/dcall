package com.dcall.core.configuration.app.service.fingerprint;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrintBean;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.verticle.fingerprint.FingerPrintConsumerVerticle;
import com.dcall.core.configuration.generic.vertx.cluster.HazelcastCluster;

public class FingerPrintServiceImpl implements FingerPrintService {
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
}
