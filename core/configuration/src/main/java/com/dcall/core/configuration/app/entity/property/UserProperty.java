package com.dcall.core.configuration.app.entity.property;

import com.dcall.core.configuration.app.entity.user.User;

public interface UserProperty extends Property {
    User getUser();
    UserProperty setUser(final User user);
}
