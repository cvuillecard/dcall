package com.dcall.core.configuration.app.service.certificate;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.generic.entity.certificate.Certificate;

import java.security.KeyPair;

public interface CertificateService {
    Certificate createUserCertificate(final UserContext context, final String path, final String salt);
    Certificate getUserCertificate(final UserContext context, final String path, final String salt);

    Certificate getUserCertificate(final UserContext context, final Certificate certificate);
}
