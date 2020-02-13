package com.dcall.core.app.terminal.gui.controller.cursor;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class CursorController {
    private static final Logger LOG = LoggerFactory.getLogger(CursorController.class);
    private static Screen screen;

    public static final void init(final Screen screen) {
        CursorController.screen = screen;
    }

    public static final void moveAt(final ScreenMetrics metrics) {
        CursorController.screen.setCursorPosition(new TerminalPosition(metrics.currX, metrics.currY));
    }

    public static final void moveAfter(final ScreenMetrics metrics) throws IOException {
        LOG.info(" cursor X = " + metrics.currX);
        LOG.info(" cursor Y = " + metrics.currY);

        if (metrics.currX == metrics.maxWidth && metrics.currY == metrics.maxHeight)
            DisplayController.scrollUp(metrics, TermAttributes.SCROLL_PADDING_UP);

        if (metrics.currX == metrics.maxWidth) {
            metrics.currX = 1;
            metrics.currY += (metrics.currY == metrics.maxHeight ? 0 : 1);
        }

        CursorController.screen.setCursorPosition(new TerminalPosition(metrics.currX + 1, metrics.currY));
        CursorController.screen.refresh();
    }
}
