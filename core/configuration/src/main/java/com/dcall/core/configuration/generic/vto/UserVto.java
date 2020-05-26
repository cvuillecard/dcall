package com.dcall.core.configuration.generic.vto;

import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.generic.entity.user.User;

public class UserVto {
    private boolean userExists = false;
    private User user = null;

    public boolean hasIdentity() {
        return user != null
                && user.getName() != null && !user.getName().isEmpty()
                && user.getSurname() != null && !user.getSurname().isEmpty()
                && user.getEmail() != null && !user.getEmail().isEmpty()
                && user.getLogin() != null && !user.getLogin().isEmpty()
                && user.getPassword() != null && !user.getPassword().isEmpty();
    }

    // getters
    public boolean userExists() { return userExists; }
    public User getUser() { return user; }

    // setters
    public void setUserExists(final boolean present) {
        this.userExists = present;
    }

    public void setUser(final User user) {
        this.user = user;
        this.userExists = hasIdentity();
        if (this.userExists())
            this.user.setPassword(
                    HashProvider.signSha512(
                            HashProvider.seedSha512(this.user.getEmail().getBytes()),
                            HashProvider.seedSha512(this.user.getPassword().getBytes())
                    )
            );
    }
}
