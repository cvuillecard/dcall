package com.dcall.core.configuration.app.provider;

import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.provider.user.UserServiceProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;

public final class ServiceProvider {
    private final HashServiceProvider hashServiceProvider = new HashServiceProvider();
    private final UserServiceProvider userServiceProvider = new UserServiceProvider(hashServiceProvider);
    private final EnvironService environService = new EnvironServiceImpl(hashServiceProvider);

    public HashServiceProvider hashServiceProvider() { return hashServiceProvider; }
    public UserServiceProvider userServiceProvider() { return userServiceProvider; }
    public EnvironService environService() { return environService; }
}
