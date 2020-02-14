package com.dcall.core.app.terminal;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TerminalApp {
    private static final Logger LOG = LoggerFactory.getLogger(TerminalApp.class);

    public static void run() {
        GUIProcessor.start();
        GUIProcessor.loop();

        LOG.debug("killed..");
    }
}
