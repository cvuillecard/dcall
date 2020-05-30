package com.dcall.core.configuration.app.provider.user;

import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;
import com.dcall.core.configuration.app.service.user.UserService;
import com.dcall.core.configuration.app.service.user.UserServiceImpl;

public final class UserServiceProvider {
    private final EnvironService environService;
    private final UserService userService;

    public UserServiceProvider() {
        this.userService = new UserServiceImpl();
        this.environService = new EnvironServiceImpl();
    }

    public UserServiceProvider(final EnvironService environService) {
        this.userService = new UserServiceImpl(environService);
        this.environService = environService;
    }

    public UserService userService() { return this.userService; }
    public EnvironService environService() { return this.environService; }
}
