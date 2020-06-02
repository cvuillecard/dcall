package com.dcall.core.configuration.generic.entity.identity;

import com.dcall.core.configuration.generic.entity.user.User;

import java.util.Properties;

public interface Identity {
    // getter
    User getUser();
    Properties getProperties();

    // setter
    Identity setUser(final User user);
    Identity setProperties(final Properties properties);
}
