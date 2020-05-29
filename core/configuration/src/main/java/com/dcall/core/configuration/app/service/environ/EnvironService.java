package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.app.context.crypto.CryptoContext;
import com.dcall.core.configuration.generic.entity.environ.Environ;
import com.dcall.core.configuration.generic.entity.user.User;

public interface EnvironService {
    Environ getOrCreateUserEnv(final User user);
}
