package com.dcall.core.configuration.app.service.user;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.generic.entity.user.User;

public interface UserService {
    boolean hasIdentity(final User user, boolean encode);
    boolean hasLogged(final User user);
    boolean hasUser(final User user);
    boolean hasConfiguration(final UserContext context);
    void encodePassword(final User user);
}
