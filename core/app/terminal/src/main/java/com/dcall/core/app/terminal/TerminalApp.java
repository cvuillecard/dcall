package com.dcall.core.app.terminal;

import com.dcall.core.app.terminal.controller.gui.GUIController;
import com.dcall.core.app.terminal.controller.gui.ScreenController;
import com.dcall.core.app.terminal.controller.gui.drawer.TextDrawer;
import com.dcall.core.app.terminal.controller.gui.handler.IOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TerminalApp {
    private static final Logger LOG = LoggerFactory.getLogger(TerminalApp.class);
    private static final IOHandler handler = new IOHandler();

    public static final void start() {
        GUIController.init();
        TerminalApp.handle();
    }

    public static final void handle() {
        GUIController.prompt(true, handler, ScreenController.metrics());
        while (ScreenController.isUp()) {

        }

        close();
    }

    private static final void close() {
        ScreenController.close();
    }
}
