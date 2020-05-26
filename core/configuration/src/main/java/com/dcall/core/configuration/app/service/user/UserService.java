package com.dcall.core.configuration.app.service.user;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.generic.entity.user.User;

public interface UserService {
    void configureUserContext(final UserContext userContext);

    boolean hasIdentity(final User user);
    boolean hasLogged(final User user);
    void encodePassword(final User user);
}
