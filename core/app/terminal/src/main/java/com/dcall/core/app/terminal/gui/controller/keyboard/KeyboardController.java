package com.dcall.core.app.terminal.gui.controller.keyboard;

import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.bus.handler.InputHandler;
import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.controller.screen.ScrollMetrics;
import com.dcall.core.app.terminal.gui.service.drawer.TextDrawer;
import com.dcall.core.configuration.constant.IOConstant;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.MARGIN_TOP;
import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.onFirstLinePos;

public final class KeyboardController {
    private static final Logger LOG = LoggerFactory.getLogger(KeyboardController.class);
    private static KeyStroke keyPressed;
    private static volatile IOHandler bus;
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

    public static int handleKeyboard(final boolean displayInputLocked) {
        readInput();
        lock = false;

        if (keyPressed != null) {
            if (!displayInputLocked)
                Stream.of(KeyboardAction.values())
                        .filter(k -> !lock && k.getKeyType().equals(KeyboardController.keyPressed.getKeyType()))
                        .forEach(KeyboardController::handleKeys);
            else
                return handleCurrentTaskKeyboardActions();
        }

        return IOConstant.EXIT_SUCCESS;
    }

    private static int handleCurrentTaskKeyboardActions() {
        if (KeyboardController.keyPressed.isCtrlDown()) {
            if (KeyboardController.keyPressed.getKeyType().equals(KeyboardAction.CTRL_EXIT.getKeyType())
                && equalsActionChar(KeyboardAction.CTRL_EXIT)) {

                DisplayController.unlock();

                return IOConstant.EXIT_FAILURE;
            }
        }

        return IOConstant.EXIT_SUCCESS;
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
            if (equalsActionChar(action))
                KeyboardController.runAction(action);
            else
                Arrays.stream(noKeyActions)
                        .filter(a -> a.equals(action)).findFirst()
                        .ifPresent(KeyboardController::runAction);
        }
    }

    private static boolean equalsActionChar(final KeyboardAction action) {
        return KeyboardController.keyPressed.getCharacter() != null &&
                (
                        ((int) KeyboardController.keyPressed.getCharacter()) == action.intValue()
                                ||
                        ((int) KeyboardController.keyPressed.getCharacter()) == (action.intValue() + 32)
                );
    }

    private static void runAction(final KeyboardAction action) {
        if (action.getFunction() != null) {
            LOG.debug("Key pressed : " + keyPressed.getCharacter() + " [ type = "+ action.getTypeAction() + " : " + action.name() + "]");
            action.getFunction().run();
            lock = true;
        }
        else
            LOG.debug("Key Pressed - not handled '" + action.name() + "' [ key = " + action.getKeyType().name() + ", type = " + action.getTypeAction() + " ]");
    }

    public static void moveAfter() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (!entry.isAppend()) {
            entry.moveAfter(" ");

            handleScrollUp(metrics, entry);

            entryToMetricsEOL(metrics, entry);

            DisplayController.moveAt(metrics);
        }
    }

    public static void moveBefore() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (onFirstLinePos(entry.posX(), entry.posY()))
            return;

        entry.moveBefore(" ");

        handleScrollDown(metrics, entry);

        if (entry.posX() <= TermAttributes.getPrompt().length() && entry.posY() == 0)
            entry.setX(TermAttributes.getPrompt().length());

        metrics.currX = metrics.screenPosX(entry.posX());
        metrics.currY = metrics.screenPosY(entry.posY());

        DisplayController.moveAt(metrics);
    }

    public static void moveStart() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        entry.setX(TermAttributes.getPrompt().length());
        entry.setY(0);

        handleScrollDown(metrics, entry);

        metrics.currX = metrics.screenPosX(entry.posX());
        metrics.currY = metrics.screenPosY(entry.posY());

        DisplayController.moveAt(metrics);
    }

    public static void moveEnd() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        entry.setX(entry.getBuffer().get(entry.maxNbLine()).size());
        entry.setY(entry.maxNbLine());

        handleScrollUp(metrics, entry);

        if (entry.posX() == TermAttributes.getTotalLineWidth()) {
            metrics.currX = metrics.screenPosX(0);
            metrics.currY = metrics.screenPosY(entry.posY() + 1);
        }
        else {
            metrics.currX = metrics.screenPosX(entry.posX());
            metrics.currY = metrics.screenPosY(entry.posY());
        }

        DisplayController.moveAt(metrics);
    }

    public static void scrollUp() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final ScrollMetrics scrollMetrics = ScreenController.scrollMetrics();

        if (bus.output().entries().size() > 0 && scrollMetrics.currBufferIdx >= 0) {
            DisplayController.scrollUp(bus, metrics, scrollMetrics, TermAttributes.getScrollPadding());
        }
    }

    public static void moveUp() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (entry.posY() > 0) {
            final int newY = entry.posY() - 1;

            entry.setY(newY);
            entry.setX(newY == 0 && entry.posX() < TermAttributes.getPrompt().length() ? TermAttributes.getPrompt().length() : entry.posX());

            handleScrollDown(metrics, entry);

            metrics.currX = metrics.screenPosX(entry.posX());
            metrics.currY = metrics.screenPosY(entry.posY());

            DisplayController.moveAt(metrics);
        }
    }

    public static void moveDown() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (entry.posY() < entry.maxNbLine()) {
            final int newY = entry.posY() + 1;

            entry.setY(newY);
            entry.setX(entry.posX() > entry.getBuffer().get(newY).size() ? entry.getBuffer().get(newY).size() : entry.posX());

            handleScrollUp(metrics, entry);

            metrics.currX = metrics.screenPosX(entry.posX());
            metrics.currY = metrics.screenPosY(entry.posY());

            DisplayController.moveAt(metrics);
        }
    }

    public static void cut() {
        final InputEntry<String> entry = bus.input().current();

        if (!entry.isAppend())
            DisplayController.cut(bus, ScreenController.metrics());
    }

    public static void paste() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();
        final String content = bus.input().clipBoard().getContent();

        if (content != null) {
            bus.input().addStrToEntry(entry, content);

            DisplayController.paste(entry, content.length(), metrics);
        }
    }

    public static void clearScreen() {
        DisplayController.clearScreen(bus, ScreenController.metrics(), ScreenController.scrollMetrics());
    }

    public static void stop() {
        ScreenController.stop();
    }

    public static void prevInput() {
        switchInput(ScreenController.metrics(), bus.input().current(), bus.input().prevEntry());
    }

    public static void nextInput() {
        switchInput(ScreenController.metrics(), bus.input().current(), bus.input().nextEntry());
    }

    private static void switchInput(final ScreenMetrics metrics, final InputEntry<String> entry, final InputEntry<String> switchEntry) {
        if (switchEntry != null) {
            if (bus.input().lastInput().getBuffer().get(0) == entry.getBuffer().get(0))
                bus.input().setLastInput(entry);
            entry.setX(TermAttributes.getPrompt().length());
            entry.setY(0);
            metrics.currX = metrics.screenPosX(entry.posX());
            metrics.currY = metrics.screenPosY(entry.posY());

            DisplayController.drawBlankFromPos(entry, metrics);

            entry.cloneBuffer(switchEntry.getBuffer());

            DisplayController.drawBlankFromPos(entry, metrics);
            DisplayController.updateScreenMetrics(entry, metrics);

            moveEnd();
        }
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

    public static void moveLeft() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final InputEntry<String> entry = bus.input().current();

        if (onFirstLinePos(entry.posX(), entry.posY()))
            return;

        entry.moveBeforeX(-1);

        DisplayController.updateScreenMetrics(entry, metrics);

        metrics.currX = metrics.screenPosX(entry.posX());
        metrics.currY = metrics.screenPosY(entry.posY());

        DisplayController.moveAt(metrics);
    }

    public static void handleCharacter() {
        if (!keyPressed.isCtrlDown() && !keyPressed.isAltDown()) {
            final String character = keyPressed.getCharacter().toString();

            bus.input().current().add(character);

            handleScrollOnKeyPress(ScreenController.metrics(), ScreenController.scrollMetrics());

            DisplayController.displayCharacter(bus.input().current(), ScreenController.metrics(), character);
        }
    }

    public static void deleteCharacter() {
        final int posX = bus.input().current().posX();
        final int posY = bus.input().current().posY();

        handleScrollOnKeyPress(ScreenController.metrics(), ScreenController.scrollMetrics());

        if (posY > 0 || (posY == 0 && posX > TermAttributes.getPrompt().length())) {
            bus.input().current().remove();
            DisplayController.deleteCharacter(bus.input().current(), ScreenController.metrics());
        }
    }

    public static void enter() {
        final ScreenMetrics metrics = ScreenController.metrics();
        final ScrollMetrics scrollMetrics = ScreenController.scrollMetrics();
        final InputEntry<String> entry = bus.input().current();

        handleScrollOnKeyPress(metrics, scrollMetrics);

        entry.setX(entry.getBuffer().get(entry.maxNbLine()).size());
        entry.setY(entry.maxNbLine());

        DisplayController.updateScreenMetrics(entry, metrics);

        metrics.minY = metrics.screenPosY(entry.nbLine());
        metrics.currY = metrics.minY;
        metrics.currX = metrics.minX;

        DisplayController.moveAt(metrics);

        if (!bus.handleInput())
            DisplayController.addPrompt(bus);
    }

    private static void handleScrollOnKeyPress(ScreenMetrics metrics, ScrollMetrics scrollMetrics) {
        if (scrollMetrics.currEntry != null && metrics.currY > metrics.maxY)
            clearScreen();
    }

    public static void handleNextInput(final boolean displayInputLocked) {
        if (handleKeyboard(displayInputLocked) == IOConstant.EXIT_SUCCESS) {
            if (bus.output().size() > 0 && bus.output().getLastIdx() < bus.output().current().nbLine()) {
                DisplayController.drawOutputEntry(bus, ScreenController.metrics());
            } else {
                if (bus.output().current().getBuffer().size() > 0)
                    DisplayController.displayPromptOnReady(bus);
            }
        }
        else {
            DisplayController.setDataReady(true);
            DisplayController.displayPromptOnReady(bus);
        }
    }

    private static void handleScrollUp(final ScreenMetrics metrics, final InputEntry<String> entry) {
        if (metrics.screenPosY(entry.posY()) > metrics.maxY) {
            DisplayController.updateScreenMetrics(entry, metrics);
            TextDrawer.drawHeader(TermAttributes.FRAME_NB_COLS);
        }
    }

    private static void handleScrollDown(final ScreenMetrics metrics, final InputEntry<String> entry) {
        if (metrics.screenPosY(entry.posY()) < MARGIN_TOP) {
            DisplayController.updateScreenMetrics(entry, metrics);
            TextDrawer.drawHeader(TermAttributes.FRAME_NB_COLS);
        }
    }

    /** UTILS **/
    private static void entryToMetricsEOL(final ScreenMetrics metrics, final InputEntry<String> entry) {
        if (entry.posX() > TermAttributes.getMaxLineWidth()) {
            metrics.currX = metrics.screenPosX(0);
            metrics.currY = metrics.screenPosY(entry.posY() + 1);
        }
        else {
            metrics.currX = metrics.screenPosX(entry.posX());
            metrics.currY = metrics.screenPosY(entry.posY());
        }
    }

}
