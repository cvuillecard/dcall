package com.dcall.core.configuration.app.context.user;

import com.dcall.core.configuration.generic.entity.Entity;
import com.dcall.core.configuration.generic.entity.user.User;

public class UserContext {
    private Entity<String> id;
    private UserInfo info = new UserInfo();
    private User user = null;

    public boolean hasIdentity() {
        return user != null
                && user.getName() != null && !user.getName().isEmpty()
                && user.getSurname() != null && !user.getSurname().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && user.getLogin() != null && !user.getLogin().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty();
    }

    public boolean hasLogged() {
        return user != null
                && user.getName() != null && !user.getName().isEmpty()
                && user.getSurname() != null && !user.getSurname().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && user.getLogin() != null && !user.getLogin().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty();
    }

    public boolean hasUser() { return user != null; }
    public User getUser() { return this.user; }
    public void setUser(final User user) { this.user = user; }
}
