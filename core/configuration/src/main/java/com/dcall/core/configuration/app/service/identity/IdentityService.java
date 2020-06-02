package com.dcall.core.configuration.app.service.identity;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.generic.entity.identity.Identity;

public interface IdentityService {
    Identity createUserIdentity(final UserContext context, final String path, final String salt);
    Identity getUserIdentity(final UserContext context, final Identity identity);
}
