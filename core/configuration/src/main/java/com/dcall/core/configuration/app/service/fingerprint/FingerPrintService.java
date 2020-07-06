package com.dcall.core.configuration.app.service.fingerprint;


import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;

public interface FingerPrintService {
    FingerPrintService publishPublicUserCertificate(final UserContext userContext);
    FingerPrintService sendCipherTransporter(RuntimeContext runtimeContext, FingerPrint<String> fingerPrint);
}
