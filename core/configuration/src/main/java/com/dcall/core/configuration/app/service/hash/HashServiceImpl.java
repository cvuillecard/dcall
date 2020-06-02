package com.dcall.core.configuration.app.service.hash;

import com.dcall.core.configuration.app.constant.SaltDef;
import com.dcall.core.configuration.generic.entity.hash.UserHash;
import com.dcall.core.configuration.generic.entity.hash.UserHashBean;
import com.dcall.core.configuration.generic.entity.user.User;

public class HashServiceImpl implements HashService {

    @Override
    public UserHash createUserHash(final User user, final String pwd, final SaltDef saltDef) {
        return new UserHashBean(pwd, user, saltDef);
    }
}
