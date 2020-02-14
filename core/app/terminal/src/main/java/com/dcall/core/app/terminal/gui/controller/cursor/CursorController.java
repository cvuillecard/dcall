package com.dcall.core.app.terminal.gui.controller.cursor;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CursorController {
    private static final Logger LOG = LoggerFactory.getLogger(CursorController.class);
    private static Screen screen;

    public static void init(final Screen screen) {
        CursorController.screen = screen;
    }

    public static void moveAt(final ScreenMetrics metrics) {
        CursorController.screen.setCursorPosition(new TerminalPosition(metrics.currX, metrics.currY));
    }

    public static void moveAfter(final ScreenMetrics metrics) {
        LOG.debug(" cursor X = " + metrics.currX);
        LOG.debug(" cursor Y = " + metrics.currY);

        if (metrics.currX  == metrics.maxWidth) {
            if (metrics.currY == metrics.maxHeight)
                DisplayController.scrollUp(metrics, TermAttributes.SCROLL_PADDING_UP);
            else {
                metrics.currX = metrics.minWidth;
                metrics.currY++;
            }
        }

        CursorController.screen.setCursorPosition(new TerminalPosition(metrics.currX, metrics.currY));
    }

    public static void moveBefore(final ScreenMetrics metrics) {
        LOG.info(" cursor X = " + metrics.currX);
        LOG.info(" cursor Y = " + metrics.currY);

        if (metrics.currX == metrics.minWidth) {
            metrics.currX = metrics.maxWidth;
            metrics.currY--;
        }
        else
            metrics.currX--;


        CursorController.moveAt(metrics);
    }
}
