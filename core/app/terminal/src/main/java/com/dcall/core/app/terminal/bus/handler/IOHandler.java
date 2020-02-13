package com.dcall.core.app.terminal.bus.handler;

public final class IOHandler {
    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();

    // GETTERS
    public final InputHandler input() { return inputHandler; }
    public final OutputHandler output() { return outputHandler; }
}
