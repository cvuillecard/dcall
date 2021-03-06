package com.dcall.core.app.terminal.gui.controller.screen;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
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
        if (metrics.currX == metrics.maxX) {
            metrics.currX = metrics.minX;
            metrics.currY++;
        }

        if (metrics.currY > metrics.maxY) {
            final int distance = metrics.currY - metrics.maxY;
            metrics.currY = metrics.maxY;
            if (distance != 0) {
                metrics.minY -= distance;
                ScreenController.getScreen().scrollLines(TermAttributes.MARGIN_TOP, metrics.maxY, distance);
            }
            ScreenController.scrollMetrics().reset();
        }

        LOG.debug(" cursor X = " + metrics.currX);
        LOG.debug(" cursor Y = " + metrics.currY);

        CursorController.screen.setCursorPosition(new TerminalPosition(metrics.currX, metrics.currY));
    }
}
