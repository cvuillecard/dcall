package com.dcall.core.configuration.generic.entity.system;

import com.dcall.core.configuration.generic.entity.Entity;

import java.util.Map;

public interface Environ<ID> extends Entity<ID> {
    Map<String, String> getEnv();
    Environ<String> setEnv(final Map<String, String> env);
}
