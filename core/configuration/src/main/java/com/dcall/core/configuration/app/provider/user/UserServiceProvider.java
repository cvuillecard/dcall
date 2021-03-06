package com.dcall.core.configuration.app.provider.user;

import com.dcall.core.configuration.app.provider.version.VersionServiceProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;
import com.dcall.core.configuration.app.service.user.UserService;
import com.dcall.core.configuration.app.service.user.UserServiceImpl;

import java.io.Serializable;

public final class UserServiceProvider implements Serializable {
    private final EnvironService environService;
    private final UserService userService;
    private final VersionServiceProvider versionServiceProvider;

    public UserServiceProvider() {
        this.environService = new EnvironServiceImpl();
        this.userService = new UserServiceImpl(environService);
        this.versionServiceProvider = new VersionServiceProvider(environService);
    }

    public UserServiceProvider(final EnvironService environService) {
        this.environService = environService;
        this.userService = new UserServiceImpl(this.environService);
        this.versionServiceProvider = new VersionServiceProvider(this.environService);
    }

    public UserServiceProvider(final VersionServiceProvider versionServiceProvider) {
        this.versionServiceProvider= versionServiceProvider;
        this.environService = this.versionServiceProvider.environService();
        this.userService = new UserServiceImpl(versionServiceProvider);
    }

    public UserService userService() { return this.userService; }
    public EnvironService environService() { return this.environService; }
    public VersionServiceProvider getVersionServiceProvider() { return this.versionServiceProvider; }
}
