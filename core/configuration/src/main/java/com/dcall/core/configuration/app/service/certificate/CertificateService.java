package com.dcall.core.configuration.app.service.certificate;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.generic.entity.certificate.Certificate;

import java.security.KeyPair;

public interface CertificateService {
    Certificate createUserCertificate(UserContext context, String path, String salt);
    Certificate getUserCertificate(UserContext context, String path, String salt);

    Certificate getUserCertificate(UserContext context, Certificate certificate, String salt);
}
