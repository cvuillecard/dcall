package com.dcall.core.configuration.app.service.identity;

import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.entity.identity.Identity;

import java.io.Serializable;

public interface IdentityService extends Serializable {
    Identity createUserIdentity(final UserContext context, final String path);
    Identity getUserIdentity(final UserContext context, final Identity identity);
    Identity updateUserIdentity(final Identity identity);
}
