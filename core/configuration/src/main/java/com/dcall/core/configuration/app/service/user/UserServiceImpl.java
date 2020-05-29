package com.dcall.core.configuration.app.service.user;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.generic.entity.user.User;

public class UserServiceImpl implements UserService {
    private final HashServiceProvider hashServiceProvider;

    public UserServiceImpl() { hashServiceProvider = new HashServiceProvider(); }
    public UserServiceImpl(final HashServiceProvider hashServiceProvider) { this.hashServiceProvider = hashServiceProvider; }

    @Override
    public boolean hasIdentity(final User user) {
        boolean state = user != null
                && user.getName() != null && !user.getName().isEmpty()
                && user.getSurname() != null && !user.getSurname().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && user.getLogin() != null && !user.getLogin().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty()
                && user.getPath() != null && !user.getPath().isEmpty();

        if (state)
            encodePassword(user);

        return state;
    }

    @Override
    public boolean hasLogged(final User user) {
        boolean state = user != null
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty();

        if (state)
            encodePassword(user);

        return state;
    }

    @Override
    public boolean hasUser(User user) {
        return hasIdentity(user) || hasLogged(user);
    }

    @Override
    public void encodePassword(final User user) {
        user.setPassword(
                HashProvider.signSha512(
                        HashProvider.seedSha512(user.getEmail().getBytes()),
                        HashProvider.seedSha512(user.getPassword().getBytes())
                )
        );
    }
}
