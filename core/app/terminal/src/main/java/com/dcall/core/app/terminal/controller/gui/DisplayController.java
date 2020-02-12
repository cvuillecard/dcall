package com.dcall.core.app.terminal.controller.gui;

import com.dcall.core.app.terminal.configuration.TermAttributes;
import com.dcall.core.app.terminal.controller.gui.drawer.TextDrawer;
import com.dcall.core.app.terminal.controller.gui.handler.IOHandler;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;

import java.io.IOException;

public final class DisplayController {

    public static final void init(final ScreenMetrics metrics) {
        TextDrawer.drawHeader(metrics.width);
        metrics.currX++;
    }

    public static void prompt(final boolean firstTime, final ScreenMetrics metrics) {
        ScreenController.getScreen().newTextGraphics().drawLine(0, metrics.currY, TermAttributes.PROMPT.length(), metrics.currY, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.PROMPT_BACKGROUND));
        ScreenController.getScreen().newTextGraphics()
                .setBackgroundColor(TermAttributes.PROMPT_BACKGROUND)
                .setForegroundColor(TermAttributes.PROMPT_FOREGROUND)
                .putString(0, metrics.currY, TermAttributes.PROMPT, SGR.BOLD);

        metrics.currX = TermAttributes.PROMPT.length() + 1;

        ScreenController.getScreen().setCursorPosition(new TerminalPosition(metrics.currX, metrics.currY));
    }
}
