package com.dcall.core.configuration.app.service.user;

import com.dcall.core.configuration.app.constant.LoginOption;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.entity.user.User;

import java.io.Serializable;

public interface UserService extends Serializable {
    User createSystemUser();
    UserService configureSystemUser(final RuntimeContext context);
    boolean hasIdentity(final User user);
    boolean hasLogged(final User user);
    boolean hasUser(final User user);
    boolean isValidUser(final User user, final LoginOption loginOption);
    boolean hasConfiguration(final RuntimeContext context);
    void initRepository(final RuntimeContext context, boolean create);
    User encodePassword(final User user);
}
