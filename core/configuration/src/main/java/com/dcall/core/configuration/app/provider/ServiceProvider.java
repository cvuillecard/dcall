package com.dcall.core.configuration.app.provider;

import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.provider.user.UserServiceProvider;

public final class ServiceProvider {
    private final HashServiceProvider hashServiceProvider = new HashServiceProvider();
    private final UserServiceProvider userServiceProvider = new UserServiceProvider();

    public HashServiceProvider hashServiceProvider() { return hashServiceProvider; }
    public UserServiceProvider userServiceProvider() { return userServiceProvider; }
}
