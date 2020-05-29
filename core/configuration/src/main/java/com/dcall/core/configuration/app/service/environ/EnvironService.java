package com.dcall.core.configuration.app.service.environ;

import com.dcall.core.configuration.generic.entity.system.Environ;
import com.dcall.core.configuration.generic.entity.user.User;

public interface EnvironService {
    Environ getOrCreateUserEnv(final User user);
}
