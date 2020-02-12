package com.dcall.core.app.terminal.controller.gui.drawer;

import com.dcall.core.app.terminal.configuration.TermAttributes;
import com.dcall.core.app.terminal.controller.gui.ScreenController;
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
}
