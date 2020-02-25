package com.dcall.core.app.terminal.gui;

import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.bus.input.InputLine;
import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.cursor.CursorController;
import com.dcall.core.app.terminal.gui.controller.keyboard.KeyboardController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.MARGIN_TOP;

public final class GUIProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(GUIProcessor.class);
    private static final IOHandler bus = new IOHandler();
    private static Terminal terminal;
    private static Screen screen;

    public static void start() {
        bus.init();
        GUIProcessor.init();
        GUIProcessor.loop();
    }

    private static void init() {
        ScreenController.init();

        terminal = ScreenController.getTerminal();
        screen = ScreenController.getScreen();

        KeyboardController.init(terminal, bus);
        CursorController.init(screen);
        DisplayController.init(ScreenController.metrics());
    }

    private static void prompt(final ScreenMetrics metrics) {
        metrics.currY = metrics.minY;

        bus.input().addEntry(TermAttributes.PROMPT);

        DisplayController.displayPrompt(metrics);
    }

    public static void flush() {
        try {
            terminal.flush();
        }
        catch (IOException e) {
            LOG.error(GUIProcessor.class.getName() + " > ERROR < " + e.getMessage());
        }
    }

    private static void loop() {

        GUIProcessor.prompt(ScreenController.metrics());

        while (ScreenController.isUp()) {
            KeyboardController.handleKeyboard();
        }

        GUIProcessor.close();
    }

    private static void close() {
        ScreenController.close();
    }

    public static void resize(final ScreenMetrics metrics) {
        bus.input().resizeCurrent();
        final InputEntry<String> entry = bus.input().current();

        if (metrics.maxY == metrics.minY) {
            ScreenController.getScreen().scrollLines(MARGIN_TOP, metrics.maxY, 1);
            metrics.minY -= 1;
        }

        final ScreenMetrics oldMetrics = new ScreenMetrics(metrics);
        oldMetrics.currX = metrics.screenPosX(TermAttributes.getPromptStartIdx());
        oldMetrics.currY = oldMetrics.minY;

        bus.input().current().setX(TermAttributes.getPromptStartIdx());
        bus.input().current().setY(0);

        DisplayController.drawBlankEntry(bus.input().current(), oldMetrics);

        entry.setX(metrics.posX());
        entry.setY(metrics.posY());

        if (entry.posY() > entry.maxNbLine())
            entry.setY(entry.maxNbLine());
        if (entry.posX() > entry.getBuffer().get(entry.posY()).size())
            entry.setX(entry.getBuffer().get(entry.posY()).size());

        metrics.currX = metrics.screenPosX(entry.posX());
        metrics.currY = metrics.screenPosY(entry.posY());

        if (entry.getBuffer().get(0).size() > TermAttributes.getPromptStartIdx())
            DisplayController.drawInputEntryFromPos(entry,  oldMetrics);
    }
}
