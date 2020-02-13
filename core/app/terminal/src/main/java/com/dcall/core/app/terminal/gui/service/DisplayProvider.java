package com.dcall.core.app.terminal.gui.service;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.cursor.CursorController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.service.drawer.TextDrawer;
import com.googlecode.lanterna.TerminalPosition;

public final class DisplayProvider {

    public static final void init(final ScreenMetrics metrics) {
        TextDrawer.drawHeader(metrics.width);
        metrics.currX++;
    }

    public static void displayPrompt(final ScreenMetrics metrics) {
        TextDrawer.drawPrompt(metrics);

        metrics.currX = TermAttributes.PROMPT.length() + 1;

        CursorController.move(metrics);
    }
}
