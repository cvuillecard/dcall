package com.dcall.core.configuration.generic.entity.hash;

import com.dcall.core.configuration.generic.entity.Entity;
import com.dcall.core.configuration.generic.entity.user.User;

public interface UserHash<ID> extends Entity<ID> {
    // getter
    String getPwd();
    String getSalt();
    String getMd5Salt();

    // setter
    UserHash<ID> setPwd(final String pwd);
    UserHash<ID> setSalt(final User user);

    // util
    String saltResource(final String name);
}
