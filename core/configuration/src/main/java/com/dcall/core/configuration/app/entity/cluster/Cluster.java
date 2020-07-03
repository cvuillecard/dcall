package com.dcall.core.configuration.app.entity.cluster;

import com.dcall.core.configuration.app.entity.Entity;

public interface Cluster<ID> extends Entity<ID> {
    String getName();
    @Deprecated String getPassword();
}
