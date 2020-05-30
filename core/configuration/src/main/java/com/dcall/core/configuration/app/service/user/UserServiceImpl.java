package com.dcall.core.configuration.app.service.user;

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
    public boolean hasUser(final User user) {
        return hasIdentity(user) || hasLogged(user);
    }

    @Override
    public boolean hasConfiguration(final User user) {
        final boolean hasConfiguration = !this.hasIdentity(user) && environService.hasConfiguration(user);

        if (!hasConfiguration)
            user.reset();

        return hasConfiguration;
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
