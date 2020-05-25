package com.dcall.core.configuration.generic.entity.cluster;

import com.dcall.core.configuration.app.security.hash.HashProvider;
import com.dcall.core.configuration.generic.entity.Entity;

public class ClusterBean implements Cluster<String> {
    private final String id;
    private final String name;
    private final String password;

    public ClusterBean(final String id, final String name, final String password) {
        this.name = HashProvider.signSha512(name);
        this.password = HashProvider.signSha512(password);
        this.id = HashProvider.signSha512(HashProvider.seedSha512(name.getBytes()), HashProvider.seedSha512(password.getBytes()));
    }

    @Override public String getName() { return name; }
    @Override public String getPassword() { return password; }
    @Override public String getId() { return id; }

    @Override
    public Entity<String> setId(final String id) { return this; }
}
