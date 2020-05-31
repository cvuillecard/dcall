package com.dcall.core.configuration.app.service.hash;

import com.dcall.core.configuration.app.constant.SaltDef;
import com.dcall.core.configuration.generic.entity.hash.UserHash;
import com.dcall.core.configuration.generic.entity.user.User;

public interface HashService {
    UserHash createUserHash(final User user, final String pwd, final SaltDef saltDef);
}
