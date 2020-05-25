package com.dcall.core.configuration.generic.entity.cluster;

import com.dcall.core.configuration.generic.entity.Entity;

public interface Cluster<ID> extends Entity<ID> {
    String getName();
    @Deprecated String getPassword();
}
