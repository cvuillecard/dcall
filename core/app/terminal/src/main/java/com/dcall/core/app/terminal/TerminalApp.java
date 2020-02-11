package com.dcall.core.app.terminal;

import com.dcall.core.app.terminal.controller.gui.GUIController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class TerminalApp {
    private static final Logger LOG = LoggerFactory.getLogger(TerminalApp.class);

    private static final void init() {
        GUIController.init();
    }

//    public static final void start() {
//        try {
//            init();
//            handle();
//        } catch (IOException e) {
//            LOG.error(TerminalApp.class.getName() + " - start() ERROR > " + e.getMessage());
//        }
//    }

    public static final boolean isUp () {
        return GUIController.isUp();
    }
}
