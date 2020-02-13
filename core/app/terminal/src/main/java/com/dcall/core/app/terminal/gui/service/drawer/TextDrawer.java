package com.dcall.core.app.terminal.gui.service.drawer;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextCharacter;

public final class TextDrawer {
    public static void drawHeader(final int width) {
        ScreenController.getScreen().newTextGraphics().drawLine(0, 0, width, 0, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.HEADER_BACKGROUND));
        ScreenController.getScreen().newTextGraphics()
                .setBackgroundColor(TermAttributes.HEADER_BACKGROUND)
                .setForegroundColor(TermAttributes.HEADER_FOREGROUND)
                .putString((width / 2) - (TermAttributes.HEADER_TITLE.length() / 2), 0,
                        TermAttributes.HEADER_TITLE, SGR.BOLD);
    }

    public static void drawPrompt(final ScreenMetrics metrics) {
        ScreenController.getScreen().newTextGraphics().drawLine(0, metrics.currY, TermAttributes.PROMPT.length(), metrics.currY, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.PROMPT_BACKGROUND));
        ScreenController.getScreen().newTextGraphics()
                .setBackgroundColor(TermAttributes.PROMPT_BACKGROUND)
                .setForegroundColor(TermAttributes.PROMPT_FOREGROUND)
                .putString(0, metrics.currY, TermAttributes.PROMPT, SGR.BOLD);
    }
}
