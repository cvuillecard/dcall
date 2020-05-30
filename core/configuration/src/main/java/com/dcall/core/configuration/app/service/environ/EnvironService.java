package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.generic.entity.environ.Environ;
import com.dcall.core.configuration.generic.entity.user.User;

public interface EnvironService {
    Environ createEnviron(final User user);
    boolean hasConfiguration(final User user);
    HashServiceProvider getHashServiceProvider();
}
