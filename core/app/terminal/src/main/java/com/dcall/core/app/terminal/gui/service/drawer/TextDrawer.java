package com.dcall.core.app.terminal.gui.service.drawer;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;

public final class TextDrawer {

    public static void drawHeader(final int width) {
        TextDrawer.textGraphics().drawLine(0, 0, width, 0, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.HEADER_BACKGROUND));

        TextDrawer.textGraphics()
                .setBackgroundColor(TermAttributes.HEADER_BACKGROUND)
                .setForegroundColor(TermAttributes.HEADER_FOREGROUND)
                .putString((width / 2) - (TermAttributes.HEADER_TITLE.length() / 2), 0,
                        TermAttributes.HEADER_TITLE, SGR.BOLD);
    }

    public static void drawPrompt(final ScreenMetrics metrics) {
        TextDrawer.textGraphics().drawLine(TermAttributes.MARGIN_LEFT, metrics.currY, TermAttributes.PROMPT.length() - 1, metrics.currY, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.PROMPT_BACKGROUND));

        TextDrawer.promptTextGraphics().putString(TermAttributes.MARGIN_LEFT, metrics.currY, TermAttributes.PROMPT, SGR.BOLD);
    }

    public static void drawString(final int x, final int y, final String character) {
        TextDrawer.inputTextGraphics().putString(x, y, character, SGR.BOLD);
    }

    public static void drawCharacter(final int x, final int y, final char c) {
        TextDrawer.textGraphics().setCharacter(new TerminalPosition(x, y), c);
    }

    // UTILS
    private static final TextGraphics textGraphics() {
        return  ScreenController.getScreen().newTextGraphics();
    }

    private static final TextGraphics inputTextGraphics() {
        return ScreenController.getScreen().newTextGraphics()
                .setBackgroundColor(TermAttributes.INPUT_BACKGROUND)
                .setForegroundColor(TermAttributes.INPUT_FOREGROUND);
    }

    private static final TextGraphics promptTextGraphics() {
        return ScreenController.getScreen().newTextGraphics()
                .setBackgroundColor(TermAttributes.PROMPT_BACKGROUND)
                .setForegroundColor(TermAttributes.PROMPT_FOREGROUND);
    }
}
