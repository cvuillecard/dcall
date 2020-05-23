package com.dcall.core.configuration.app.provider.hash;

import com.dcall.core.configuration.app.service.hash.HashFileService;
import com.dcall.core.configuration.app.service.hash.HashFileServiceImpl;

public final class HashServiceProvider {
    private final HashFileService hashFileServiceImpl = new HashFileServiceImpl();

    public HashFileService hashFileService() { return hashFileServiceImpl; }
}
