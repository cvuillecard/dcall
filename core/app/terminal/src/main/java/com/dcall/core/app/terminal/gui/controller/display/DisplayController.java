package com.dcall.core.app.terminal.gui.controller.display;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.cursor.CursorController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.service.drawer.TextDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class DisplayController {
    private static final Logger LOG = LoggerFactory.getLogger(DisplayController.class);

    public static final void init(final ScreenMetrics metrics) {
        TextDrawer.drawHeader(metrics.width);
        metrics.currX++;
    }

    public static void displayPrompt(final ScreenMetrics metrics) {
        TextDrawer.drawPrompt(metrics);

        metrics.currX = TermAttributes.PROMPT.length() + 1;

        CursorController.moveAt(metrics);

        ScreenController.refresh();
    }

    public static void displayCharacter(final ScreenMetrics metrics, final String character) {
        try {
            LOG.debug("character : " + character);

            CursorController.moveAfter(metrics);

            TextDrawer.drawCharacter(metrics, character);

            ScreenController.refresh();
        }
        catch (IOException e) {
            LOG.error(DisplayController.class.getName() + " > ERROR < " + e.getMessage());
        }
    }

    public static final int moveX(final ScreenMetrics metrics) {
        try {
            if (metrics.currX + 1 == ScreenController.getTerminal().getTerminalSize().getColumns()) {
                metrics.currY += 1;
                metrics.currX = 1;

                return metrics.currX;
            }
        }
        catch (IOException e) {
            LOG.error(DisplayController.class.getName() + " > ERROR < " + e.getMessage());
        }

        return metrics.currX++;
    }

    public static void scrollUp(final ScreenMetrics metrics, final int distance) {
        ScreenController.getScreen().scrollLines(1, metrics.height, distance);
    }

    public static void scrollDown(final ScreenMetrics metrics, final int distance) {
        ScreenController.getScreen().scrollLines(1, metrics.height, (distance > 0 ? distance * -1 : distance));
    }
}
