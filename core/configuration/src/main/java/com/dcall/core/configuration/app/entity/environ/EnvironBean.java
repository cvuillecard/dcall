package com.dcall.core.configuration.app.entity.environ;

import com.dcall.core.configuration.app.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.app.entity.cipher.CipherAES;
import com.dcall.core.configuration.app.entity.user.User;

import java.util.Properties;

public final class EnvironBean extends AbstractCipherResource<String> implements Environ {
    private User user;
    private Properties properties = new Properties();

    public EnvironBean() { super(); }

    public EnvironBean(final String path, final CipherAES<String> cipherAES) { super(path, cipherAES); }

    public EnvironBean(final String path, final CipherAES<String> cipherAES, final User user) {
        super(path, cipherAES);
        this.setUser(user);
    }

    public EnvironBean(final String id, final String path, final CipherAES<String> cipherAES, final User user) {
        super(id, path, cipherAES);
        this.setUser(user);
    }

    // getter
    @Override public User getUser() { return this.user; }
    @Override public Properties getProperties() { return this.properties; }

    // setter
    @Override public Environ setUser(final User user) { this.user = user; return this; }
    @Override public Environ setProperties(final Properties properties) { this.properties = properties; return this; }
}
