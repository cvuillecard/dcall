package com.dcall.core.app.terminal.gui.controller.keyboard;

import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Stream;

public final class KeyboardController {
    private static final Logger LOG = LoggerFactory.getLogger(KeyboardController.class);
    private static KeyStroke keyPressed;
    private static IOHandler bus;
    private static Terminal term;

    public static final void init(final Terminal term, final IOHandler bus) {
        KeyboardController.term = term;
        KeyboardController.bus = bus;
    }

    private static final void readInput() {
        try {
            KeyboardController.keyPressed = KeyboardController.term.pollInput();
        }
        catch (IOException e) {
            LOG.error(KeyboardController.class.getName() + " > ERROR < " + e.getMessage());
        }
    }

    public static final void handleKeyboard() {
        readInput();
        if (keyPressed != null) {
            Stream.of(KeyboardAction.values())
                    .filter(k -> k.getKeyType().equals(KeyboardController.keyPressed.getKeyType()))
                    .forEach(action -> handleKeys(action));

//            GUIProcessor.flush(); // SLOW PERF
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
            if (KeyboardController.keyPressed.isCtrlDown() && KeyboardController.keyPressed.getCharacter() != null &&
                    (
                            ((int) KeyboardController.keyPressed.getCharacter().charValue()) == action.intValue()
                            ||
                            ((int) KeyboardController.keyPressed.getCharacter().charValue()) == (action.intValue() + 32)
                    ))
                KeyboardController.runAction(action);
    }

    private static final void runAction(final KeyboardAction action) {
        if (action.getFunction() != null) {
            LOG.debug("Key pressed : " + keyPressed.getCharacter().charValue() + " [ type = "+ action.getTypeAction() + " ]");
            action.getFunction().run();
        }
        else
            LOG.debug("Key Pressed - not handled " + "[ type = " + action.getTypeAction() + " ]");
    }

    public static final void handleCharacter() {
        final String character = keyPressed.getCharacter().toString();
        bus.input().current().add(character);
        DisplayController.displayCharacter(ScreenController.metrics(), character);
    }

    public static final void stop() {
        ScreenController.stop();
    }

}
