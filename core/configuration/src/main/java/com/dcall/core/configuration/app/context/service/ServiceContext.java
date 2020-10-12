package com.dcall.core.configuration.app.context.service;

import com.dcall.core.configuration.app.provider.ServiceProvider;

import java.io.Serializable;

public final class ServiceContext implements Serializable {
    private final ServiceProvider serviceProvider = new ServiceProvider();

    public ServiceProvider serviceProvider() { return serviceProvider; }
}
