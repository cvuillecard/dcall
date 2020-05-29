package com.dcall.core.configuration.generic.entity.environ;

import com.dcall.core.configuration.generic.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class EnvironBean implements Environ<String> {
    private String id;
    private Map<String, String> env = new HashMap<>();

    // getters
    @Override public String getId() { return this.id; }
    @Override public Map<String, String> getEnv() { return this.env; }

    // setters
    @Override public Entity<String> setId(final String id) { this.id = id; return this; }
    @Override public Environ<String> setEnv(final Map<String, String> env) { this.env = env; return this; }
}
