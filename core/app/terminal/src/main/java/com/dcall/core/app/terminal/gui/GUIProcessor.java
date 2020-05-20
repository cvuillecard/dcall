package com.dcall.core.app.terminal.gui;

import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.CursorController;
import com.dcall.core.app.terminal.gui.controller.keyboard.KeyboardController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.app.terminal.gui.service.credential.window.UserCredentialDrawer;
import com.dcall.core.configuration.vertx.VertxApplication;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import io.vertx.core.Vertx;
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

        bus.initCredentials();
    }

    private static void prompt(final ScreenMetrics metrics) {
        metrics.currY = metrics.minY;

        bus.input().addEntry(TermAttributes.getPrompt());

        DisplayController.displayPrompt(metrics);
    }

    private static void loop() {
        if (bus.hasUser()) {
            try {
                screen.setCursorPosition(null);
                terminal.clearScreen();
                GUIProcessor.prompt(ScreenController.metrics());
                handleIO();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        } else
            configureUser();
    }

    private static void configureUser() {
        Vertx.currentContext().executeBlocking(
                future -> future.complete(new UserCredentialDrawer(screen, bus.credentials()).build()),
                handler -> loop()
        );
    }

    private static void handleIO() {
        Vertx.currentContext().executeBlocking(future -> future.complete(ScreenController.isUp()), handler -> {
            if (handler.succeeded()) {
                if (handler.result().equals(true)) {
                    if (DisplayController.getLock())
                        KeyboardController.handleNextInput(true);
                    else {
                        KeyboardController.handleKeyboard(false);
                    }
                    handleIO();
                } else
                    GUIProcessor.close();
            } else {
                LOG.error(GUIProcessor.class.getName() + " > IOHandler() : " + handler.cause().getMessage());
            }
        });
    }

    private static void close() {
        ScreenController.close();
        VertxApplication.shutdown();
    }

    public static void resize(final ScreenMetrics metrics) {
        bus.input().resizeCurrent();
        final InputEntry<String> entry = bus.input().current();

        if (metrics.maxY < metrics.minY) {
            final int distance = metrics.minY - metrics.maxY;
            ScreenController.getScreen().scrollLines(MARGIN_TOP, metrics.maxY, distance);
            metrics.minY -= distance;
        }

        final ScreenMetrics oldMetrics = new ScreenMetrics(metrics);
        oldMetrics.currX = metrics.screenPosX(TermAttributes.getPrompt().length());
        oldMetrics.currY = oldMetrics.minY;

        bus.input().current().setX(TermAttributes.getPrompt().length());
        bus.input().current().setY(0);

        DisplayController.drawBlankEntry(bus.input().current(), oldMetrics);
        DisplayController.displayPrompt(metrics);

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

    public static IOHandler bus() { return bus; }
}
