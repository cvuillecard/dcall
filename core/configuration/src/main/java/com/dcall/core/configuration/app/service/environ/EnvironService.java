package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.entity.environ.Environ;

import java.util.Properties;

public interface EnvironService {
    boolean hasConfiguration(final UserContext context);

    Environ createEnviron(UserContext context, String path);
    Environ createUserEnviron(UserContext context, boolean create);
    String createPublicId(UserContext context);

    Properties loadEnvironProperties(UserContext context);

    String getConfigDirName();
    String getConfigDirectory();
    HashServiceProvider getHashServiceProvider();
    String getEnvProperty(final Environ environ, final String key);
    Environ setEnvProperty(final Environ environ, final String key, final String value);
    Environ updateEnviron(final Environ environ);
}
