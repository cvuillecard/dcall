package com.dcall.core.configuration.app.provider.user;

import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;
import com.dcall.core.configuration.app.service.user.UserService;
import com.dcall.core.configuration.app.service.user.UserServiceImpl;
import com.dcall.core.configuration.generic.entity.system.Environ;

public final class UserServiceProvider {
    private final EnvironService environService;
    private final UserService userService;

    public UserServiceProvider() {
        userService = new UserServiceImpl();
        environService = new EnvironServiceImpl();
    }
    public UserServiceProvider(final HashServiceProvider hashServiceProvider) {
        userService = new UserServiceImpl(hashServiceProvider);
        environService = new EnvironServiceImpl(hashServiceProvider);
    }

    public UserService userService() { return this.userService; }
    public EnvironService environService() { return this.environService; }
}
