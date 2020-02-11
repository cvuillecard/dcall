package com.dcall.core.app.terminal.gui;

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

public final class ScreenController {
    private static final Logger LOG = LoggerFactory.getLogger(ScreenController.class);
    private static boolean up = true;
    private static Screen screen;
    private static Terminal terminal;
    private static int width;
    private static int height;
    private static int limitWidth;
    private static int limitHeight;

    public static final void init() {
        initScreen();
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
        term.addResizeListener(new SimpleTerminalResizeListener(new TerminalSize(TermAttributes.FRAME_NB_COLS, TermAttributes.FRAME_NB_ROWS)) {
            @Override
            public synchronized void onResized(final Terminal terminal, final TerminalSize newSize) {
                super.onResized(terminal, newSize);
                try {
                    LOG.debug(" *** Window resized to : "
                            + terminal.getTerminalSize().getColumns() + " x "
                            + terminal.getTerminalSize().getRows());
                    setScreenSize(terminal.getTerminalSize().getColumns(), terminal.getTerminalSize().getRows());
                    ((SwingTerminalFrame)TerminalUI.term).setTitle(TermAttributes.FRAME_TITLE + " (" + width + 'x' + height + ')');
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

            setScreenSize(TermAttributes.FRAME_NB_COLS, TermAttributes.FRAME_NB_ROWS);

            screen.startScreen();
            screen.setCursorPosition(null);

            LOG.debug(ScreenController.class.getName() + " > initScreen() > done.");
            LOG.debug(ScreenController.class.getName() + "    | width = " + width + " columns");
            LOG.debug(ScreenController.class.getName() + "    | height = " + height + " rows");
        } catch (final IOException e) {
            LOG.error(e.getMessage());
        }
    }

    private static final void setScreenSize(final int nbCols, final int nbRows) {
        width = nbCols;
        height = nbRows;
        limitWidth = width - 1;
        limitHeight = height - 1;
    }
}
