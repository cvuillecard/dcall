package com.dcall.core.configuration.app.provider;

import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.provider.user.UserServiceProvider;
import com.dcall.core.configuration.app.provider.version.VersionServiceProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;

public final class ServiceProvider {
    private final HashServiceProvider hashServiceProvider = new HashServiceProvider();
    private final EnvironService environService = new EnvironServiceImpl(hashServiceProvider);
    private final VersionServiceProvider versionServiceProvider = new VersionServiceProvider(environService);
    private final UserServiceProvider userServiceProvider = new UserServiceProvider(versionServiceProvider);

    public HashServiceProvider hashServiceProvider() { return hashServiceProvider; }
    public EnvironService environService() { return environService; }
    public VersionServiceProvider versionService() { return versionServiceProvider; }
    public UserServiceProvider userServiceProvider() { return userServiceProvider; }
}
