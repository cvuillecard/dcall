package com.dcall.core.app.terminal.gui.service.drawer;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.HEADER_BACKGROUND;
import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.MARGIN;

public final class TextDrawerService {

    public static void drawHeader(final int width) {
        final int startX = (width / 2) - (TermAttributes.HEADER_TITLE.length() / 2);

        TextDrawerService.textGraphics().drawLine(0, 0, width, 0, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.HEADER_BACKGROUND));

        TextDrawerService.textGraphics()
                .setBackgroundColor(TermAttributes.HEADER_BACKGROUND)
                .setForegroundColor(TermAttributes.HEADER_FOREGROUND)
                .putString(startX, 0,
                        TermAttributes.HEADER_TITLE, SGR.BOLD);

        TextDrawerService.drawBlank(0, MARGIN, width, MARGIN);
    }

    public static void drawPrompt(final ScreenMetrics metrics) {
        final String prompt = TermAttributes.getPrompt();
        final int separatorIdx = prompt.indexOf('@');
        final String user = prompt.substring(0, separatorIdx);
        final String host = prompt.substring(separatorIdx + 1, prompt.length() - 2);
        final String suffix = prompt.substring(user.length() + 1 + host.length(), prompt.length());

        TextDrawerService.textGraphics().drawLine(TermAttributes.MARGIN_LEFT, metrics.currY, prompt.length() - 1, metrics.currY, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.PROMPT_BACKGROUND));

        TextDrawerService.promptTextGraphics().putString(TermAttributes.MARGIN_LEFT, metrics.currY, user, SGR.BOLD);

        TextDrawerService.textGraphics().putString(TermAttributes.MARGIN_LEFT + user.length(), metrics.currY, "@", SGR.BOLD);

        TextDrawerService.promptTextGraphics().putString(TermAttributes.MARGIN_LEFT + user.length() + 1, metrics.currY, host, SGR.BOLD);

        TextDrawerService.textGraphics().setForegroundColor(HEADER_BACKGROUND)
                .putString(TermAttributes.MARGIN_LEFT + user.length() + 1 + host.length(), metrics.currY, suffix, SGR.BOLD);
    }

    public static void drawBlank(final int startX, final int startY, final int endX, final int endY) {
        TextDrawerService.inputTextGraphics().drawLine(startX, startY, endX, endY, new TextCharacter(' '));
    }

    public static void drawBlankRectangle(final int startX, final int startY, final int nbCols, final int nbRows) {
        TextDrawerService.inputTextGraphics().drawRectangle(new TerminalPosition(startX, startY), new TerminalSize(nbCols, nbRows), new TextCharacter(' '));
    }

    public static void drawInputString(final int x, final int y, final String s) {
        TextDrawerService.inputTextGraphics().putString(x, y, s, SGR.BOLD);
    }

    public static void drawOutputString(final int x, final int y, final String s) {
        TextDrawerService.inputTextGraphics().putString(x, y, s);
    }

    public static void drawCharacter(final int x, final int y, final char c) {
        TextDrawerService.textGraphics().setCharacter(new TerminalPosition(x, y), c);
    }

    // UTILS
    private static TextGraphics textGraphics() {
        return  ScreenController.getScreen().newTextGraphics();
    }

    private static TextGraphics inputTextGraphics() {
        return ScreenController.getScreen().newTextGraphics()
                .setBackgroundColor(TermAttributes.INPUT_BACKGROUND)
                .setForegroundColor(TermAttributes.INPUT_FOREGROUND);
    }

    private static TextGraphics promptTextGraphics() {
        return ScreenController.getScreen().newTextGraphics()
                .setBackgroundColor(TermAttributes.PROMPT_BACKGROUND)
                .setForegroundColor(TermAttributes.PROMPT_FOREGROUND);
    }
}
