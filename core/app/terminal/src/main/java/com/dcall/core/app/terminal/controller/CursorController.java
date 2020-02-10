package com.dcall.core.app.terminal.controller;

public final class TermController {
    private static final StringBuilder buffer = new StringBuilder();

    public static final void clearBuffer() {
        buffer.setLength(0);
    }

    public static final StringBuilder getBuffer() {
        return buffer;
    }
}
