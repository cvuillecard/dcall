package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.provider.hash.HashServiceProvider;
import com.dcall.core.configuration.app.entity.environ.Environ;

import java.io.Serializable;
import java.util.Properties;

public interface EnvironService extends Serializable {
    Environ createEnviron(final UserContext context, final String path);
    Environ updateEnviron(final Environ environ);
    boolean hasConfiguration(final UserContext context);
    Environ configureUserEnviron(final RuntimeContext context, boolean create);
    String createPublicId(final UserContext context);
    String createHostFilesDirectory(final RuntimeContext context);
    Properties loadEnvironProperties(final UserContext context);

    // getter
    String getConfigDirName();
    String getConfigDirectory();
    String getPublicId(final RuntimeContext runtimeContext);
    String getHostFilesDirectory(RuntimeContext runtimeContext);
    boolean getInterpretMode(RuntimeContext runtimeContext);
    boolean getAutoCommitMode(RuntimeContext runtimeContext);
    boolean getHostFilesMode(RuntimeContext runtimeContext);
    String getEnvProperty(final Environ environ, final String key);
    HashServiceProvider getHashServiceProvider();
    String getHostedUserPath(final RuntimeContext runtimeContext, final String publicId);
    String getHostedFilePath(final RuntimeContext runtimeContext, final String publicId, final String fileName);

    // setter
    Environ setEnvProperty(final Environ environ, final String key, final String value);
}
