package com.dcall.core.configuration.credential;

import com.dcall.core.configuration.entity.identity.Identity;

public class CredentialInfo {
    private boolean user = false;
    private Identity identity = null;

    public boolean hasIdentity() {
        return identity != null
                && identity.getName() != null && !identity.getName().isEmpty()
                && identity.getSurname() != null && !identity.getSurname().isEmpty()
                && identity.getEmail() != null && !identity.getEmail().isEmpty()
                && identity.getLogin() != null && !identity.getLogin().isEmpty()
                && identity.getPassword() != null && !identity.getPassword().isEmpty();
    }

    // getters
    public boolean hasUser() { return user; }
    public Identity getIdentity() { return identity; }

    // setters
    public void setUser(final boolean present) { this.user = present; }

    public void setIdentity(final Identity identity) {
        this.identity = identity;
        this.user = hasIdentity() ? true : false;
    }
}
