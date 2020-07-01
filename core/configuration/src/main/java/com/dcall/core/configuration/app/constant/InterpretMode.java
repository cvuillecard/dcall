package com.dcall.core.configuration.app.constant;

public enum InterpretMode { LOCAL("local"), REMOTE("remote");
    private final String mode;

    InterpretMode(final String mode) { this.mode = mode; }

    public String mode() { return mode; }
}
