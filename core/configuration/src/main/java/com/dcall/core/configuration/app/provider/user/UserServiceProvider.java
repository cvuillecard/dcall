package com.dcall.core.configuration.app.provider.user;

import com.dcall.core.configuration.app.service.user.UserService;
import com.dcall.core.configuration.app.service.user.UserServiceImpl;

public final class UserServiceProvider {
    private final UserService userService = new UserServiceImpl();

    public UserService userService() { return this.userService; }
}
