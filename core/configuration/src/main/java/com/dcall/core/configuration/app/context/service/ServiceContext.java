package com.dcall.core.configuration.app.context.service;

import com.dcall.core.configuration.app.provider.ServiceProvider;

public final class ServiceContext {
    private final ServiceProvider serviceProvider = new ServiceProvider();

    public ServiceProvider serviceProvider() { return serviceProvider; }
}
