package com.dcall.core.configuration.app.constant;

public enum GitCommitMode { AUTO(true), MANUAL(false);
    private final boolean mode;

    GitCommitMode(final boolean mode) { this.mode = mode; }

    public boolean mode() { return mode; }
}
