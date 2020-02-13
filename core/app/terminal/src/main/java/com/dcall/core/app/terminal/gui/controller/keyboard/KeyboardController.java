package com.dcall.core.app.terminal.gui.controller.keyboard;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.dcall.core.app.terminal.gui.controller.keyboard.TypeAction.CTRL;

public final class KeyboardController {
    private static final Logger LOG = LoggerFactory.getLogger(KeyboardController.class);
    private static KeyStroke keyPressed;
    private static Terminal term;

    public static final void init(final Terminal term) {
        KeyboardController.term = term;
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
        KeyboardController.readInput();
        if (KeyboardController.keyPressed != null) {
            Stream.of(KeyboardAction.values())
                    .filter(k -> k.getKeyType().equals(KeyboardController.keyPressed.getKeyType()))
                    .forEach(action -> handleKeys(action));
        }
    }

    private static void handleKeys(final KeyboardAction action) {
        switch (action.getTypeAction()) {
            case CTRL:
                KeyboardController.handleCTRLKey(action);
                break;
            default:
                LOG.debug("Key Pressed - not handled");
                LOG.debug("[ type = " + action.getTypeAction() + " ]");
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
            LOG.debug("Key pressed : " + keyPressed.getCharacter().charValue());
            LOG.debug("[ type = " + action.getTypeAction() + " ]");
            action.getFunction().run();
        }
    }

}
