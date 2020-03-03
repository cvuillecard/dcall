package com.dcall.core.app.client.terminal.gui.service.drawer;

import com.dcall.core.app.client.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.client.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.client.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.client.terminal.gui.controller.display.DisplayController;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;

import static com.dcall.core.app.client.terminal.gui.configuration.TermAttributes.MARGIN;

public final class TextDrawer {

    public static void drawHeader(final int width) {
        final int startX = (width / 2) - (TermAttributes.HEADER_TITLE.length() / 2);

        TextDrawer.textGraphics().drawLine(0, 0, width, 0, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.HEADER_BACKGROUND));

        TextDrawer.textGraphics()
                .setBackgroundColor(TermAttributes.HEADER_BACKGROUND)
                .setForegroundColor(TermAttributes.HEADER_FOREGROUND)
                .putString(startX, 0,
                        TermAttributes.HEADER_TITLE, SGR.BOLD);

        TextDrawer.drawBlank(0, MARGIN, width, MARGIN);
    }

    public static void drawPrompt(final ScreenMetrics metrics) {
        TextDrawer.textGraphics().drawLine(TermAttributes.MARGIN_LEFT, metrics.currY, TermAttributes.PROMPT.length() - 1, metrics.currY, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.PROMPT_BACKGROUND));

        TextDrawer.promptTextGraphics().putString(TermAttributes.MARGIN_LEFT, metrics.currY, TermAttributes.PROMPT, SGR.BOLD);
    }

    public static void drawBlank(final int startX, final int startY, final int endX, final int endY) {
        TextDrawer.inputTextGraphics().drawLine(startX, startY, endX, endY, new TextCharacter(' '));
    }

    public static void drawBlankRectangle(final int startX, final int startY, final int nbCols, final int nbRows) {
        TextDrawer.inputTextGraphics().drawRectangle(new TerminalPosition(startX, startY), new TerminalSize(nbCols, nbRows), new TextCharacter(' '));
    }

    public static void drawInputString(final int x, final int y, final String s) {
        TextDrawer.inputTextGraphics().putString(x, y, s, SGR.BOLD);
    }

    public static void drawOutputString(final int x, final int y, final String s) {
        TextDrawer.inputTextGraphics().putString(x, y, s);
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
