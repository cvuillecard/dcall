package com.dcall.core.app.terminal.controller.gui;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GUIController { // IOHandler -> InputHandler::InputEntries[INPUT_PAGE_SIZE] / OutputHandler::OutputEntries[OUTPUT_PAGE_SIZE] -> KeyBoardController
    private static final Logger LOG = LoggerFactory.getLogger(GUIController.class);

    private static Terminal terminal;
    private static KeyStroke keyPressed;
    private static Screen screen;

    public static final void init() {
        ScreenController.init();
        terminal = ScreenController.getTerminal();
        screen = ScreenController.getScreen();
    }

    public static final boolean isUp() {
        return ScreenController.isUp();
    }
}
