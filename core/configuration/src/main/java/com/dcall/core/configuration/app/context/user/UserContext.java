package com.dcall.core.configuration.app.context.user;

import com.dcall.core.configuration.generic.entity.certificate.Certificate;
import com.dcall.core.configuration.generic.entity.environ.Environ;
import com.dcall.core.configuration.generic.entity.hash.UserHash;
import com.dcall.core.configuration.generic.entity.user.User;

public class UserContext {
    private User user = null;
    private UserHash<String> userHash;
    private Environ environ = null;
    private Certificate certificate;

    // getter
    public User getUser() { return this.user; }
    public UserHash<String> getUserHash() { return userHash; }
    public Environ getEnviron() { return this.environ; }
    public Certificate getCertificate() { return this.certificate; }
    // setter
    public UserContext setUser(final User user) { this.user = user; return this; }
    public UserContext setUserHash(final UserHash<String> userHash) { this.userHash = userHash; return this; }
    public UserContext setEnviron(final Environ environ) { this.environ = environ; return this; }
    public void setCertificate(final Certificate certificate) { this.certificate = certificate; }
}
