package com.dcall.core.configuration.app.service.user;

import com.dcall.core.configuration.app.constant.LoginOption;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;
import com.dcall.core.configuration.generic.entity.user.User;

public class UserServiceImpl implements UserService {
    private final HashServiceProvider hashServiceProvider;
    private final EnvironService environService;

    public UserServiceImpl() {
        this.hashServiceProvider = new HashServiceProvider();
        this.environService = new EnvironServiceImpl(this.hashServiceProvider);
    }

    public UserServiceImpl(final EnvironService environService) {
        this.hashServiceProvider = environService.getHashServiceProvider();
        this.environService = environService;
    }

    @Override
    public boolean hasIdentity(final User user) {
        return user != null
                && user.getName() != null && !user.getName().isEmpty()
                && user.getSurname() != null && !user.getSurname().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && user.getLogin() != null && !user.getLogin().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty()
                && user.getPath() != null && !user.getPath().isEmpty();
    }

    @Override
    public boolean hasLogged(final User user) {
        return user != null
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty();
    }

    @Override
    public boolean hasUser(final User user) {
        return hasIdentity(user) || hasLogged(user);
    }

    @Override
    public boolean isValidUser(final User user, final LoginOption loginOption) {
        return
                (loginOption.equals(LoginOption.NEW_USER) && hasIdentity(user))
                        ||
                (loginOption.equals(LoginOption.LOGIN) && hasLogged(user));
    }

    @Override
    public boolean hasConfiguration(final UserContext context) {
        final boolean hasConfiguration = !this.hasIdentity(encodePassword(context.getUser())) && environService.hasConfiguration(context);

        if (!hasConfiguration)
            context.getUser().reset();
        else {
            environService.configureEnviron(context, false);
        }

        return hasConfiguration;
    }

    @Override
    public User encodePassword(final User user) {
        return hasLogged(user) ? user.setPassword(
                HashProvider.signSha512(
                        HashProvider.seedSha512(user.getEmail().getBytes()),
                        HashProvider.seedSha512(user.getPassword().getBytes())
                )
        ) : user;
    }
}
