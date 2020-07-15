package com.dcall.core.configuration.app.constant;

public enum AllowHostFilesMode { ON(true), OFF(false);
    private final boolean mode;

    AllowHostFilesMode(final boolean mode) { this.mode = mode; }

    public boolean mode() { return mode; }

    @Override public String toString() { return String.valueOf(mode); }
}
