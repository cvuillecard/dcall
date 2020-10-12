package com.dcall.core.configuration.app.service.hash;

import com.dcall.core.configuration.app.constant.SaltDef;
import com.dcall.core.configuration.app.entity.hash.UserHash;
import com.dcall.core.configuration.app.entity.user.User;

import java.io.Serializable;

public interface HashService extends Serializable {
    UserHash createUserHash(final User user, final String pwd, final SaltDef saltDef);
}
