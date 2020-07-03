package com.dcall.core.configuration.app.entity.identity;

import com.dcall.core.configuration.app.constant.UserConstant;
import com.dcall.core.configuration.app.entity.cipher.AbstractCipherResource;
import com.dcall.core.configuration.app.entity.cipher.CipherAES;
import com.dcall.core.configuration.app.entity.user.User;

import java.util.Properties;

public final class IdentityBean extends AbstractCipherResource<String> implements Identity {
    private User user;
    private Properties properties;

    public IdentityBean() { super(); }

    public IdentityBean(final String path, final CipherAES<String> cipherAES) { super(path, cipherAES); }

    public IdentityBean(final String path, final CipherAES<String> cipherAES, final User user) {
        super(path, cipherAES);
        this.setUser(user);
    }

    public IdentityBean(final String id, final String path, final CipherAES<String> cipherAES, final User user) {
        super(id, path, cipherAES);
        this.setUser(user);
    }

    // getter
    @Override public User getUser() { return this.user; }
    @Override public Properties getProperties() { return this.properties; }

    // setter
    @Override public Identity setUser(final User user) {
        this.user = user;
        this.properties = new Properties();

        if (this.user.getName() != null)
            this.properties.setProperty(UserConstant.NAME, this.user.getName());
        if (this.user.getSurname() != null)
            this.properties.setProperty(UserConstant.SURNAME, this.user.getSurname());
        if (this.user.getEmail() != null)
            this.properties.setProperty(UserConstant.EMAIL, this.user.getEmail());
        if (this.user.getLogin() != null)
            this.properties.setProperty(UserConstant.LOGIN, this.user.getLogin());
        if (this.user.getWorkspace() != null)
            this.properties.setProperty(UserConstant.WORKSPACE, this.user.getWorkspace());

        return this;
    }
    @Override public Identity setProperties(final Properties properties) {
        this.properties = properties;

        user.setName(this.properties.getProperty(UserConstant.NAME));
        user.setSurname(this.properties.getProperty(UserConstant.SURNAME));
        user.setEmail(this.properties.getProperty(UserConstant.EMAIL));
        user.setLogin(this.properties.getProperty(UserConstant.LOGIN));
        user.setWorkspace(this.properties.getProperty(UserConstant.WORKSPACE));

        return this;
    }
}
