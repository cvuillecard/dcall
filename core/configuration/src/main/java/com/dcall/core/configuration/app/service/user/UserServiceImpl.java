package com.dcall.core.configuration.app.service.user;

import com.dcall.core.configuration.app.constant.GitMessage;
import com.dcall.core.configuration.app.constant.LoginOption;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.provider.version.VersionServiceProvider;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;
import com.dcall.core.configuration.app.entity.user.User;

public class UserServiceImpl implements UserService {
    private final HashServiceProvider hashServiceProvider;
    private final EnvironService environService;
    private final VersionServiceProvider versionServiceProvider;

    public UserServiceImpl() {
        this.hashServiceProvider = new HashServiceProvider();
        this.environService = new EnvironServiceImpl(this.hashServiceProvider);
        this.versionServiceProvider = new VersionServiceProvider(this.environService);
    }

    public UserServiceImpl(final EnvironService environService) {
        this.hashServiceProvider = environService.getHashServiceProvider();
        this.environService = environService;
        this.versionServiceProvider = new VersionServiceProvider(this.environService);
    }

    public UserServiceImpl(final VersionServiceProvider versionServiceProvider) {
        this.hashServiceProvider = versionServiceProvider.environService().getHashServiceProvider();
        this.environService = versionServiceProvider.environService();
        this.versionServiceProvider = versionServiceProvider;
    }

    @Override
    public boolean hasIdentity(final User user) {
        return user != null
                && user.getName() != null && !user.getName().isEmpty()
                && user.getSurname() != null && !user.getSurname().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && user.getLogin() != null && !user.getLogin().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty();
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
    public boolean hasConfiguration(final RuntimeContext context) {
        initRepository(context, false);
        final boolean hasConfiguration = !this.hasIdentity(encodePassword(context.userContext().getUser())) && environService.hasConfiguration(context.userContext());

        if (!hasConfiguration)
            context.userContext().getUser().reset();
        else
            environService.createUserEnviron(context.userContext(), false);

        return hasConfiguration;
    }

    @Override
    public void initRepository(final RuntimeContext context, final boolean create) {
        final boolean isGitRepository = versionServiceProvider.gitService().isGitRepository(versionServiceProvider.gitService().getSystemRepository());
        if (context.systemContext().versionContext().getRepository() == null &&
                ((create && !isGitRepository) || (!create && isGitRepository))) {
            context.systemContext().versionContext().setRepository(versionServiceProvider.gitService().createSystemRepository(context));
        }
        else if (create) {
            context.systemContext().versionContext().setRepository(versionServiceProvider.gitService().createSystemRepository(context));
            versionServiceProvider.gitService()
                    .commitSystemRepository(context, context.systemContext().versionContext().getRepository(),
                            GitMessage.getLocalSnapshotUserMsg(context.userContext().getUser(), "New User"));
        }
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
