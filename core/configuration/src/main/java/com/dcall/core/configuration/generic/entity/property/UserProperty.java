package com.dcall.core.configuration.generic.entity.property;

import com.dcall.core.configuration.generic.entity.user.User;

public interface UserProperty extends Property {
    User getUser();
    UserProperty setUser(final User user);
}
