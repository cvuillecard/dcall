package com.dcall.core.app.terminal.gui.controller.keyboard;

import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.onFirstLinePos;
import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.screenPosY;

public final class KeyboardController {
    private static final Logger LOG = LoggerFactory.getLogger(KeyboardController.class);
    private static KeyStroke keyPressed;
    private static IOHandler bus;
    private static Terminal term;
    private static volatile Boolean lock;

    private static final KeyboardAction[] noKeyActions = new KeyboardAction[] {
            KeyboardAction.CTRL_MOVE_UP,
            KeyboardAction.CTRL_MOVE_DOWN,
            KeyboardAction.CTRL_MOVE_RIGHT,
            KeyboardAction.CTRL_MOVE_LEFT
    };


    public static void init(final Terminal term, final IOHandler bus) {
        KeyboardController.term = term;
        KeyboardController.bus = bus;
    }

    private static void readInput() {
        try {
            KeyboardController.keyPressed = KeyboardController.term.pollInput();
        }
        catch (IOException e) {
            LOG.error(KeyboardController.class.getName() + " > ERROR < " + e.getMessage());
        }
    }

    public static void handleKeyboard() {
        readInput();
        lock = false;
        if (keyPressed != null) {
            Stream.of(KeyboardAction.values())
                    .filter(k -> !lock && k.getKeyType().equals(KeyboardController.keyPressed.getKeyType()))
                    .forEach(KeyboardController::handleKeys);
        }
    }

    private static void handleKeys(final KeyboardAction action) {
        switch (action.getTypeAction()) {
            case CTRL:
                KeyboardController.handleCTRLKey(action);
                break;
            default:
                KeyboardController.runAction(action);
                break;
        }
    }

    private static void handleCTRLKey(final KeyboardAction action) {
        if (KeyboardController.keyPressed.isCtrlDown()) {
            if (KeyboardController.keyPressed.getCharacter() != null &&
                    (
                            ((int) KeyboardController.keyPressed.getCharacter()) == action.intValue()
                                    ||
                            ((int) KeyboardController.keyPressed.getCharacter()) == (action.intValue() + 32)
                    )
                )
                KeyboardController.runAction(action);
            else
                Arrays.stream(noKeyActions)
                        .filter(a -> a.equals(action)).findFirst()
                        .ifPresent(KeyboardController::runAction);
        }
    }

    private static void runAction(final KeyboardAction action) {
        if (action.getFunction() != null) {
            LOG.debug("Key pressed : " + keyPressed.getCharacter() + " [ type = "+ action.getTypeAction() + " : " + action.name() + "]");
            action.getFunction().run();
            lock = true;
        }
        else
            LOG.debug("Key Pressed - not handled " + "[ type = " + action.getTypeAction() + " ]");
    }

    public static void handleCharacter() {
        final String character = keyPressed.getCharacter().toString();

        bus.input().current().add(character);

        DisplayController.displayCharacter(bus.input().current(), ScreenController.metrics(), character);
    }

    public static void deleteCharacter() {
        final int posX = bus.input().current().posX();
        final int posY = bus.input().current().posY();

        if (posY > 0 || (posY == 0 && posX > TermAttributes.PROMPT.length())) {
            bus.input().current().remove();
            DisplayController.deleteCharacter(bus.input().current(), ScreenController.metrics());
        }
    }

    public static void moveStart() {
        final ScreenMetrics metrics = ScreenController.metrics();

        bus.input().current().setX(TermAttributes.PROMPT.length());
        bus.input().current().setY(0);

        metrics.currX = TermAttributes.screenPosX(bus.input().current().posX());
        metrics.currY = TermAttributes.screenPosY(bus.input().current().posY());

        DisplayController.moveAt(metrics);
    }

    public static void moveEnd() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        entry.setX(entry.getBuffer().get(entry.maxNbLine()).size());
        entry.setY(entry.maxNbLine());

        if (entry.posX() == TermAttributes.getTotalLineWidth()) {
            metrics.currX = TermAttributes.screenPosX(0);
            metrics.currY = TermAttributes.screenPosY(entry.posY() + 1);
        }
        else {
            metrics.currX = TermAttributes.screenPosX(entry.posX());
            metrics.currY = TermAttributes.screenPosY(entry.posY());
        }

        DisplayController.moveAt(metrics);
    }

    public static void moveDown() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (entry.posY() < entry.maxNbLine()) {
            final int newY = entry.posY() + 1;

            entry.setY(newY);
            entry.setX(entry.posX() > entry.getBuffer().get(newY).size() ? entry.getBuffer().get(newY).size() : entry.posX());

            metrics.currX = TermAttributes.screenPosX(entry.posX());
            metrics.currY = TermAttributes.screenPosY(entry.posY());

            DisplayController.moveAt(metrics);
        }
    }

    public static void moveUp() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (entry.posY() > 0) {
            final int newY = entry.posY() - 1;

            entry.setY(newY);
            entry.setX(newY == 0 && entry.posX() < TermAttributes.PROMPT.length() ? TermAttributes.PROMPT.length() : entry.posX());

            metrics.currX = TermAttributes.screenPosX(entry.posX());
            metrics.currY = TermAttributes.screenPosY(entry.posY());

            DisplayController.moveAt(metrics);
        }
    }

    public static void moveLeft() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (onFirstLinePos(entry.posX(), entry.posY()))
            return;

        entry.moveBeforeX(-1);

        metrics.currX = TermAttributes.screenPosX(entry.posX());
        metrics.currY = TermAttributes.screenPosY(entry.posY());

        DisplayController.moveAt(metrics);
    }


    public static void moveRight() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (entry.posY() == entry.maxNbLine() && entry.posX() == entry.getBuffer().get(entry.posY()).size())
            return;

        entry.moveAfterX(1);

        entryToMetricsEOL(metrics, entry);

        DisplayController.moveAt(metrics);
    }

    public static void moveAfter() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (!entry.isAppend()) {
            entry.moveAfter(" ");

            entryToMetricsEOL(metrics, entry);

            DisplayController.moveAt(metrics);
        }
    }

    private static void entryToMetricsEOL(ScreenMetrics metrics, InputEntry<String> entry) {
        if (entry.posX() > TermAttributes.getMaxLineWidth()) {
            metrics.currX = TermAttributes.screenPosX(0);
            metrics.currY = TermAttributes.screenPosY(entry.posY() + 1);
        }
        else {
            metrics.currX = TermAttributes.screenPosX(entry.posX());
            metrics.currY = TermAttributes.screenPosY(entry.posY());
        }
    }

    public static void moveBefore() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (onFirstLinePos(entry.posX(), entry.posY()))
            return;

        entry.moveBefore(" ");

        if (entry.posX() <= TermAttributes.PROMPT.length() && entry.posY() == 0)
            entry.setX(TermAttributes.PROMPT.length());

        metrics.currX = TermAttributes.screenPosX(entry.posX());
        metrics.currY = TermAttributes.screenPosY(entry.posY());

        DisplayController.moveAt(metrics);
    }

    public static void stop() {
        ScreenController.stop();
    }

}
