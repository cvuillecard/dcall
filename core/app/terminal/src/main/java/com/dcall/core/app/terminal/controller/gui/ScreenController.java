package com.dcall.core.app.terminal.controller.gui;

import com.dcall.core.app.terminal.configuration.TermAttributes;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.SimpleTerminalResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.IOException;

public final class ScreenController { // DrawHandler -> TextDrawer / WindowDrawer / CursorDrawer
    private static final Logger LOG = LoggerFactory.getLogger(ScreenController.class);

    private static volatile boolean up = true;
    private static volatile ScreenMetrics metrics = new ScreenMetrics();
    private static Screen screen;
    private static Terminal terminal;

    public static final void init() {
        initScreen();
    }

    public static final void close() {
        try {
            screen.stopScreen();
            screen.close();
        } catch (IOException e) {
            LOG.error(TerminalUI.class.getName() + " - close() ERROR > " + e.getMessage());
        }
    }

    private static final void resetPosition() {
        metrics.currX = 0;
        metrics.currY = TermAttributes.MARGIN_TOP;
    }

    public static final void refresh() {
        try {
            screen.doResizeIfNecessary();
            screen.refresh();
        } catch (IOException e) {
            LOG.error(ScreenController.class.getName() + " - ERROR > " + e.getMessage());
        }
    }

    private static void addWindowListener() {
        ((SwingTerminalFrame) terminal).addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                ScreenController.up = false;
            }
        });
    }

    private static final void addWindowListeners() {
        if (terminal instanceof SwingTerminalFrame) {
            ((SwingTerminalFrame) terminal).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            addWindowListener();
            addResizeListener();
        }
    }

    private static void addResizeListener() {
        ScreenController.terminal.addResizeListener(new SimpleTerminalResizeListener(new TerminalSize(TermAttributes.FRAME_NB_COLS, TermAttributes.FRAME_NB_ROWS)) {
            @Override
            public synchronized void onResized(final Terminal terminal, final TerminalSize newSize) {
                super.onResized(terminal, newSize);
                try {
                    LOG.debug(" *** Window resized to : "
                            + terminal.getTerminalSize().getColumns() + " x "
                            + terminal.getTerminalSize().getRows());
                    setScreenSize(terminal.getTerminalSize().getColumns(), terminal.getTerminalSize().getRows());
                    ((SwingTerminalFrame)ScreenController.terminal).setTitle(TermAttributes.FRAME_TITLE + " (" + metrics.width + 'x' + metrics.height + ')');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final void initScreen() {
        try {
            terminal = new DefaultTerminalFactory()
                    .setTerminalEmulatorTitle(TermAttributes.FRAME_TITLE)
                    .setInitialTerminalSize(new TerminalSize(TermAttributes.FRAME_NB_COLS, TermAttributes.FRAME_NB_ROWS))
                    .createTerminal();

            addWindowListeners();

            screen = new TerminalScreen(terminal);

            resetScreenSize();

            screen.startScreen();
            screen.setCursorPosition(null);

            LOG.debug(ScreenController.class.getName() + " > initScreen() > done.");
            LOG.debug(ScreenController.class.getName() + "    | width = " + metrics.width + " columns");
            LOG.debug(ScreenController.class.getName() + "    | height = " + metrics.height + " rows");
        } catch (final IOException e) {
            LOG.error(e.getMessage());
        }
    }

    private static final void setScreenSize(final int nbCols, final int nbRows) {
        TermAttributes.FRAME_NB_COLS = nbCols;
        TermAttributes.FRAME_NB_ROWS = nbRows;
        metrics.width = nbCols;
        metrics.height = nbRows;
        metrics.maxWidth = metrics.width - TermAttributes.MARGIN;
        metrics.maxHeight = metrics.height - TermAttributes.MARGIN;
        metrics.minWidth = TermAttributes.MARGIN;
        metrics.minHeight = TermAttributes.MARGIN_TOP;
    }

    private static final void resetScreenSize() {
        TermAttributes.FRAME_NB_COLS = TermAttributes.DEF_FRAME_NB_COLS;
        TermAttributes.FRAME_NB_ROWS = TermAttributes.DEF_FRAME_NB_ROWS;
        metrics.width = TermAttributes.FRAME_NB_COLS;
        metrics.height = TermAttributes.FRAME_NB_ROWS;
        metrics.maxWidth = metrics.width - TermAttributes.MARGIN;
        metrics.maxHeight = metrics.height - TermAttributes.MARGIN;
        metrics.minWidth = TermAttributes.MARGIN;
        metrics.minHeight = TermAttributes.MARGIN_TOP;
    }

    // GETTERS
    public static final boolean isUp() { return up; }
    public static final ScreenMetrics metrics() { return metrics; }

    public static final Screen getScreen() { return screen; }
    public static final Terminal getTerminal() { return terminal; }
}
