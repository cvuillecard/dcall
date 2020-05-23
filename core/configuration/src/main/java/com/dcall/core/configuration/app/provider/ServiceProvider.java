package com.dcall.core.configuration.app.provider;

import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;

public final class ServiceProvider {
    private final HashServiceProvider hashServiceProvider = new HashServiceProvider();

    public HashServiceProvider hashServiceProvider() { return hashServiceProvider; }
}
