package com.dcall.core.configuration.app.context.user;

import com.dcall.core.configuration.generic.entity.system.Environ;
import com.dcall.core.configuration.generic.entity.user.User;

public class UserContext {
    private User user = null;
    private Environ environ = null;

    public User getUser() { return this.user; }
    public Environ getEnviron() { return this.environ; }
    public void setUser(final User user) { this.user = user; }
    public UserContext setEnviron(final Environ environ) { this.environ = environ; return this; }
}
