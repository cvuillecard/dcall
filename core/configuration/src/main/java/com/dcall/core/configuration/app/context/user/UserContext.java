package com.dcall.core.configuration.app.context.user;

import com.dcall.core.configuration.app.entity.certificate.Certificate;
import com.dcall.core.configuration.app.entity.environ.Environ;
import com.dcall.core.configuration.app.entity.hash.UserHash;
import com.dcall.core.configuration.app.entity.identity.Identity;
import com.dcall.core.configuration.app.entity.user.User;

import java.io.Serializable;

public class UserContext implements Serializable {
    private User user = null;
    private UserHash<String> userHash;
    private Environ environ = null;
    private Identity identity;
    private Certificate certificate;

    // getter
    public User getUser() { return this.user; }
    public UserHash<String> getUserHash() { return userHash; }
    public Environ getEnviron() { return this.environ; }
    public Identity getIdentity() { return this.identity; }
    public Certificate getCertificate() { return this.certificate; }

    // setter
    public UserContext setUser(final User user) { this.user = user; return this; }
    public UserContext setUserHash(final UserHash<String> userHash) { this.userHash = userHash; return this; }
    public UserContext setEnviron(final Environ environ) { this.environ = environ; return this; }
    public UserContext setIdentity(final Identity identity) { this.identity = identity; return this; }
    public UserContext setCertificate(final Certificate certificate) { this.certificate = certificate; return this; }
}
