package com.dcall.core.configuration.app.service.user;

import com.dcall.core.configuration.app.constant.LoginOption;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.generic.entity.user.User;

public interface UserService {
    boolean hasIdentity(final User user);
    boolean hasLogged(final User user);
    boolean hasUser(final User user);
    boolean isValidUser(final User user, final LoginOption loginOption);
    boolean hasConfiguration(final UserContext context);
    User encodePassword(final User user);
}
