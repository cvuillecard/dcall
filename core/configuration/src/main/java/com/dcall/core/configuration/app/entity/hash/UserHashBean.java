package com.dcall.core.configuration.app.entity.hash;

import com.dcall.core.configuration.app.constant.SaltDef;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.app.entity.Entity;
import com.dcall.core.configuration.app.entity.user.User;

public final class UserHashBean implements UserHash<String> {
    private String id;
    private String pwd; // path working directory
    private String salt;
    private String md5Salt;

    public UserHashBean() {}

    public UserHashBean(final User user, final String pwd, final SaltDef saltDef) {
        this.pwd = pwd;
        this.setSalt(user, saltDef);
    }

    public UserHashBean(final String id, final String pwd, final User user, final SaltDef saltDef) {
        this.id = id;
        this.pwd = pwd;
        this.setSalt(user, saltDef);
    }

    // getter
    @Override public String getId() { return this.id; }
    @Override public String getPwd() { return this.pwd; }
    @Override public String getSalt() { return this.salt; }
    @Override public String getMd5Salt() { return this.md5Salt; }

    // setter
    @Override
    public Entity<String> setId(final String s) {
        this.id = id;

        return this;
    }

    @Override public UserHash<String> setPwd(final String pwd) {
        this.pwd = pwd;

        return this;
    }

    @Override
    public UserHash<String> setSalt(final User user, final SaltDef saltDef) {
        this.salt = HashProvider.createSalt512(user.getEmail(), user.getPassword(), saltDef.getSalt());
        this.md5Salt = HashProvider.seedMd5(this.salt.getBytes());

        return this;
    }

    @Override public String saltResource(final String name) { return HashProvider.createSalt512(salt, name); }
}
