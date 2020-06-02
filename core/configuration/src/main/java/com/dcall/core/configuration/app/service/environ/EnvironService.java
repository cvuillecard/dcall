package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.generic.entity.environ.Environ;

public interface EnvironService {
    Environ configureEnviron(final UserContext context, final boolean create);
    boolean hasConfiguration(final UserContext context);
    String getConfigDirectory();
    HashServiceProvider getHashServiceProvider();
}
