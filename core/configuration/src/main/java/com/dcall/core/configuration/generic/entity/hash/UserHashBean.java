package com.dcall.core.configuration.generic.entity.hash;

import com.dcall.core.configuration.app.constant.SaltConstant;
import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.generic.entity.Entity;
import com.dcall.core.configuration.generic.entity.user.User;

public class UserHashBean implements UserHash<String> {
    private String id;
    private String pwd;
    private String salt;
    private String md5Salt;

    public UserHashBean() {}

    public UserHashBean(final String pwd, final User user) {
        this.pwd = pwd;
        this.setSalt(user);
    }

    public UserHashBean(final String id, final String pwd, final User user) {
        this.id = id;
        this.pwd = pwd;
        this.setSalt(user);
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
    public UserHash<String> setSalt(final User user) {
        this.salt = HashProvider.createSalt512(user.getEmail(), user.getPassword(), SaltConstant.SALT_USER);
        this.md5Salt = HashProvider.seedMd5(this.salt.getBytes());

        return this;
    }

    @Override public String saltResource(final String name) { return HashProvider.createSalt512(salt, name); }
}
