package com.dcall.core.app.terminal.gui;

import com.dcall.core.app.terminal.TerminalApp;
import com.dcall.core.app.terminal.configuration.TermAttributes;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.SimpleTerminalResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GUIController {
    private static final Logger LOG = LoggerFactory.getLogger(GUIController.class);

    private static Terminal term;
    private static KeyStroke keyPressed;
    private static Screen screen;

    public static final void init() {

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

}
