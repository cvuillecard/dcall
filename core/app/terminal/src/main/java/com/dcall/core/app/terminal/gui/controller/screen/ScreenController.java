package com.dcall.core.app.terminal.gui.controller.screen;

import com.dcall.core.app.terminal.gui.configuration.ScreenAttributes;
import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.configuration.generic.cluster.vertx.VertxApplication;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.SimpleTerminalResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.*;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.getMinScreenHeight;
import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.getMinScreenWidth;

public final class ScreenController {
    private static final Logger LOG = LoggerFactory.getLogger(ScreenController.class);

    private static volatile boolean up = true;
    private static volatile ScreenMetrics metrics = new ScreenMetrics();
    private static volatile ScrollMetrics scrollMetrics = new ScrollMetrics();
    private static Screen screen;
    private static Terminal terminal;
    private static ScreenAttributes.FrameType frameType;

    public static void init(final ScreenAttributes.FrameType frameType) {
        ScreenController.frameType = frameType;
        initScreen();
    }

    public static void stop() {
        ScreenController.up =  false;
    }

    public static void close() {
        try {
            screen.stopScreen();
            screen.close();
        } catch (IOException e) {
            LOG.error(ScreenController.class.getName() + " - close() ERROR > " + e.getMessage());
        }
    }

    public static void refresh() {
        try {
            screen.doResizeIfNecessary();
            screen.refresh();
        } catch (IOException e) {
            LOG.error(ScreenController.class.getName() + " - ERROR > " + e.getMessage());
        }
    }

    private static void addWindowListener() {
        ((Frame) terminal).addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                ScreenController.up = false;
            }
        });
    }

    private static void addWindowListeners() {
        if (terminal instanceof SwingTerminalFrame)
            ((SwingTerminalFrame) terminal).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            addWindowListener();
            addResizeListener();
            addCloseListener();
    }

    private static void addCloseListener() {
        final Vertx vertx = Vertx.currentContext().owner();

        ((Frame) terminal).addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                VertxApplication.shutdown(vertx);
                close();
                LOG.warn(" > Swing Terminal Frame closing");
                super.windowClosing(e);
            }
        });
    }

    private static void addResizeListener() {
        setMinimumSize();
        ScreenController.terminal.addResizeListener(new SimpleTerminalResizeListener(new TerminalSize(TermAttributes.FRAME_NB_COLS, TermAttributes.FRAME_NB_ROWS)) {
            @Override
            public synchronized void onResized(final Terminal terminal, final TerminalSize newSize) {
                try {
                    super.onResized(terminal, newSize);
                    LOG.debug(" *** Window resized to : " + terminal.getTerminalSize().getColumns() + " x " + terminal.getTerminalSize().getRows());

                    setScreenMetrics(terminal.getTerminalSize().getColumns(), terminal.getTerminalSize().getRows());
                    ((Frame)ScreenController.terminal).setTitle(TermAttributes.FRAME_TITLE + " (" + metrics.width + 'x' + metrics.height + ')');

                    DisplayController.resize(ScreenController.metrics());
//                    if (terminal.getTerminalSize().getColumns() < getMinScreenWidth())
//                        ((SwingTerminalFrame)ScreenController.terminal).setSize(((SwingTerminalFrame) ScreenController.terminal).getMinimumSize());
                } catch (IOException e) {
                    LOG.error(ScreenController.class.getName() + " > ERROR < " + e.getMessage());
                }
            }
        });
    }

    private static void setMinimumSize() {
        final Dimension dimension = new Dimension();
        dimension.setSize(getMinScreenWidth(), getMinScreenHeight());
        ((Frame) ScreenController.terminal).setPreferredSize(dimension);
    }

    private static void initScreen() {
        try {
            final TerminalEmulatorDeviceConfiguration configuration = new TerminalEmulatorDeviceConfiguration()
                    .withCursorStyle(TerminalEmulatorDeviceConfiguration.CursorStyle.VERTICAL_BAR)
                    .withCursorBlinking(true).withBlinkLengthInMilliSeconds(TermAttributes.DEFAULT_BLINKING_DURATION_MS);

            terminal = new DefaultTerminalFactory()
                    .setTerminalEmulatorFrameAutoCloseTrigger(TerminalEmulatorAutoCloseTrigger.CloseOnExitPrivateMode)
                    .setTerminalEmulatorTitle(TermAttributes.FRAME_TITLE)
                    .setTerminalEmulatorDeviceConfiguration(configuration)
                    .setTerminalEmulatorFontConfiguration(AWTTerminalFontConfiguration.newInstance(TermAttributes.DEFAULT_FONT_POLICY))
                    .setForceAWTOverSwing(ScreenController.frameType.equals(ScreenAttributes.FrameType.AWT_FRAME))
                    .setInitialTerminalSize(new TerminalSize(TermAttributes.FRAME_NB_COLS, TermAttributes.FRAME_NB_ROWS))
                    .createTerminal();

            setMinimumSize();
            addWindowListeners();

            screen = new TerminalScreen(terminal);

            initScreenMetrics();

            screen.startScreen();
            screen.setCursorPosition(null);

            LOG.debug(ScreenController.class.getName() + " > initScreen() > done.");
            LOG.debug(ScreenController.class.getName() + "    | width = " + metrics.width + " columns");
            LOG.debug(ScreenController.class.getName() + "    | height = " + metrics.height + " rows");
        } catch (final IOException e) {
            LOG.error(e.getMessage());
        }
    }

    private static void setScreenMetrics(final int nbCols, final int nbRows) {
        TermAttributes.FRAME_NB_COLS = nbCols;
        TermAttributes.FRAME_NB_ROWS = nbRows;
        metrics.width = nbCols;
        metrics.height = nbRows;
        metrics.maxX = metrics.width - TermAttributes.MARGIN;
        metrics.maxY = metrics.height - TermAttributes.MARGIN;
        metrics.minX = TermAttributes.MARGIN;
        metrics.currX = metrics.currX > metrics.maxX ? metrics.maxX : metrics.currX;
        metrics.currY = metrics.currY > metrics.maxY ? metrics.maxY : metrics.currY;
    }

    public static void initScreenMetrics() {
        TermAttributes.FRAME_NB_COLS = TermAttributes.DEF_FRAME_NB_COLS;
        TermAttributes.FRAME_NB_ROWS = TermAttributes.DEF_FRAME_NB_ROWS;
        resetScreenMetrics();
    }

    public static void resetScreenMetrics() {
        metrics.width = TermAttributes.FRAME_NB_COLS;
        metrics.height = TermAttributes.FRAME_NB_ROWS;
        metrics.maxX = metrics.width - TermAttributes.MARGIN;
        metrics.maxY = metrics.height - TermAttributes.MARGIN;
        metrics.minX = TermAttributes.MARGIN;
        metrics.minY = TermAttributes.MARGIN_TOP;
    }

    public static void hideCursor() {
        screen.setCursorPosition(null);
    }

    // GETTERS
    public static boolean isUp() { return up; }
    public static ScreenMetrics metrics() { return metrics; }
    public static ScrollMetrics scrollMetrics() { return scrollMetrics; }

    public static Screen getScreen() { return screen; }
    public static Terminal getTerminal() { return terminal; }
}
