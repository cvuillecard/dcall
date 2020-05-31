package com.dcall.core.configuration.app.provider.hash;

import com.dcall.core.configuration.app.service.hash.HashFileService;
import com.dcall.core.configuration.app.service.hash.HashFileServiceImpl;
import com.dcall.core.configuration.app.service.hash.HashService;
import com.dcall.core.configuration.app.service.hash.HashServiceImpl;

public final class HashServiceProvider {
    private final HashService hashService = new HashServiceImpl();
    private final HashFileService hashFileServiceImpl = new HashFileServiceImpl();

    public HashService hashService() { return hashService; }
    public HashFileService hashFileService() { return hashFileServiceImpl; }
}
