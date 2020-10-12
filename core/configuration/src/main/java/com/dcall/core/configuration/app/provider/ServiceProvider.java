package com.dcall.core.configuration.app.provider;

import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.provider.message.MessageServiceProvider;
import com.dcall.core.configuration.app.provider.user.UserServiceProvider;
import com.dcall.core.configuration.app.provider.version.VersionServiceProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;

import java.io.Serializable;

public final class ServiceProvider implements Serializable {
    private final HashServiceProvider hashServiceProvider = new HashServiceProvider();
    private final EnvironService environService = new EnvironServiceImpl(hashServiceProvider);
    private final VersionServiceProvider versionServiceProvider = new VersionServiceProvider(environService);
    private final UserServiceProvider userServiceProvider = new UserServiceProvider(versionServiceProvider);
    private final MessageServiceProvider messageServiceProvider = new MessageServiceProvider(userServiceProvider);

    public HashServiceProvider hashServiceProvider() { return hashServiceProvider; }
    public EnvironService environService() { return environService; }
    public VersionServiceProvider versionServiceProvider() { return versionServiceProvider; }
    public UserServiceProvider userServiceProvider() { return userServiceProvider; }
    public MessageServiceProvider messageServiceProvider() { return messageServiceProvider; }
}
