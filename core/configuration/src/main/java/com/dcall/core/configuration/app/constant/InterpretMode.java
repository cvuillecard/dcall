package com.dcall.core.configuration.app.constant;

public enum InterpretMode { LOCAL(true), REMOTE(false);
    private final boolean mode;

    InterpretMode(final boolean mode) { this.mode = mode; }

    public boolean mode() { return mode; }
}
