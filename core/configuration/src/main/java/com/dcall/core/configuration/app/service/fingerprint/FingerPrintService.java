package com.dcall.core.configuration.app.service.fingerprint;


import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.fingerprint.FingerPrintContext;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.Message;
import com.dcall.core.configuration.app.exception.FunctionalException;

public interface FingerPrintService {
    FingerPrintService publishPublicUserCertificate(final UserContext userContext);
    FingerPrintService sendPublicUserCertificate(final RuntimeContext runtimeContext, final Message<String> fromMessage);
    FingerPrintService sendSecretKey(final RuntimeContext runtimeContext, final FingerPrint<String> fingerPrint, final Message<String> fromMessage);
    FingerPrintService sendPublicId(RuntimeContext runtimeContext, FingerPrint<String> fromFingerPrint, Message<String> fromMessage);

    FingerPrint updateCipherFingerPrint(final FingerPrint<String> fingerPrint);
    FingerPrint nextFingerPrint(final FingerPrintContext fingerPrintContext) throws FunctionalException;
}
