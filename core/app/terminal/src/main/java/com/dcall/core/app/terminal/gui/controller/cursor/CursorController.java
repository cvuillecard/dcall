package com.dcall.core.app.terminal.gui.controller.cursor;

import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.screen.Screen;

public final class CursorController {
    private static Screen screen;

    public static void init(final Screen screen) {
        CursorController.screen = screen;
    }

    public static void move(final ScreenMetrics metrics) {
        CursorController.screen.setCursorPosition(new TerminalPosition(metrics.currX, metrics.currY));
    }
}
