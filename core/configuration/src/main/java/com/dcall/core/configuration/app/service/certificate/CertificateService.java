package com.dcall.core.configuration.app.service.certificate;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.generic.entity.user.User;

public interface CertificateService {
    String createUserCertificate(UserContext context, String path, String salt);
}
