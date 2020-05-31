package com.dcall.core.configuration.app.service.identity;

import com.dcall.core.configuration.generic.entity.user.User;

public interface IdentityService {
    String createUserIdentity(final User user, final String path, final String salt);
}
