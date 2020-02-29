package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.TerminalApp;
import io.vertx.core.AbstractVerticle;

public class TerminalApplicationVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        TerminalApp.run();
    }
}
