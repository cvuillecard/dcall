package com.dcall.core.configuration.app.provider.hash;

import com.dcall.core.configuration.app.service.certificate.CertificateService;
import com.dcall.core.configuration.app.service.certificate.CertificateServiceImpl;
import com.dcall.core.configuration.app.service.cipher.CipherService;
import com.dcall.core.configuration.app.service.cipher.CipherServiceImpl;
import com.dcall.core.configuration.app.service.hash.HashFileService;
import com.dcall.core.configuration.app.service.hash.HashFileServiceImpl;
import com.dcall.core.configuration.app.service.hash.HashService;
import com.dcall.core.configuration.app.service.hash.HashServiceImpl;
import com.dcall.core.configuration.app.service.identity.IdentityService;
import com.dcall.core.configuration.app.service.identity.IdentityServiceImpl;

public final class HashServiceProvider {
    private final CipherService cipherService = new CipherServiceImpl();
    private final HashService hashService = new HashServiceImpl();
    private final HashFileService hashFileService = new HashFileServiceImpl();
    private final IdentityService identityService = new IdentityServiceImpl(this);
    private final CertificateService certificateService = new CertificateServiceImpl(this);

    public CipherService cipherService() { return cipherService; }
    public HashService hashService() { return hashService; }
    public HashFileService hashFileService() { return hashFileService; }
    public IdentityService identityService() { return identityService; }
    public CertificateService certificateService() { return certificateService; }
}
