package com.dcall.core.configuration.app.service.fingerprint;


import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.Message;

public interface FingerPrintService {
    FingerPrintService publishPublicUserCertificate(final UserContext userContext);

    FingerPrintService sendPublicUserCertificate(final RuntimeContext runtimeContext, final Message<String> fromMessage);

    FingerPrintService sendCipherTransporter(final RuntimeContext runtimeContext, final FingerPrint<String> fingerPrint, final Message<String> fromMessage);
}
