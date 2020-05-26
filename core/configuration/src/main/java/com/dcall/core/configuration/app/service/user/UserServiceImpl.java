package com.dcall.core.configuration.app.service.user;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.generic.entity.user.User;

public class UserServiceImpl implements UserService {
    @Override
    public void configureUserContext(final UserContext userContext) {

    }

    @Override
    public boolean hasIdentity(final User user) {
        boolean state = user != null
                && user.getName() != null && !user.getName().isEmpty()
                && user.getSurname() != null && !user.getSurname().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && user.getLogin() != null && !user.getLogin().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty();

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
    public void encodePassword(final User user) {
        user.setPassword(
                HashProvider.signSha512(
                        HashProvider.seedSha512(user.getEmail().getBytes()),
                        HashProvider.seedSha512(user.getPassword().getBytes())
                )
        );
    }
}
