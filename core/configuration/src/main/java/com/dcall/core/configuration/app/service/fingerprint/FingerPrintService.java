package com.dcall.core.configuration.app.service.fingerprint;


import com.dcall.core.configuration.app.context.user.UserContext;

public interface FingerPrintService {
    FingerPrintService publishPublicUserCertificate(final UserContext userContext);

}
