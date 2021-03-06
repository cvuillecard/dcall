package com.dcall.core.app.terminal.gui.controller.keyboard;

import com.googlecode.lanterna.input.KeyType;

public enum KeyboardAction {
    CTRL_MOVE_RIGHT(KeyType.ArrowRight, TypeAction.CTRL, null, KeyboardController::moveAfter),
    CTRL_MOVE_LEFT(KeyType.ArrowLeft, TypeAction.CTRL, null, KeyboardController::moveBefore),
    CTRL_MOVE_START(KeyType.Character, TypeAction.CTRL, "A", KeyboardController::moveStart),
    CTRL_MOVE_END(KeyType.Character, TypeAction.CTRL, "E", KeyboardController::moveEnd),
    CTRL_MOVE_UP(KeyType.ArrowUp, TypeAction.CTRL, null, KeyboardController::moveUp),
    CTRL_MOVE_DOWN(KeyType.ArrowDown, TypeAction.CTRL, null, KeyboardController::moveDown),
    CTRL_CUT(KeyType.Character, TypeAction.CTRL, "K", KeyboardController::cut),
    CTRL_PASTE(KeyType.Character, TypeAction.CTRL, "Y", KeyboardController::paste),
    CTRL_CLEAR_SCREEN(KeyType.Character, TypeAction.CTRL, "L", KeyboardController::clearScreen),
    CTRL_EXIT(KeyType.Character, TypeAction.CTRL, "C", KeyboardController::stop),
    MOVE_UP(KeyType.ArrowUp, TypeAction.MOVE, null, KeyboardController::prevInput),
    MOVE_DOWN(KeyType.ArrowDown, TypeAction.MOVE, null, KeyboardController::nextInput),
    MOVE_RIGHT(KeyType.ArrowRight, TypeAction.MOVE, null, KeyboardController::moveRight),
    MOVE_LEFT(KeyType.ArrowLeft, TypeAction.MOVE, null, KeyboardController::moveLeft),
    MOVE_START(KeyType.Home, TypeAction.MOVE, null, KeyboardController::moveStart),
    MOVE_END(KeyType.End, TypeAction.MOVE, null, KeyboardController::moveEnd),
//    SCROLL_UP(KeyType.ArrowUp, TypeAction.SCROLL, null, null),
//    SCROLL_DOWN(KeyType.ArrowDown, TypeAction.SCROLL, null, null),
    SCROLL_UP(KeyType.PageUp, TypeAction.SCROLL, null, KeyboardController::scrollUp),
    SCROLL_DOWN(KeyType.PageDown, TypeAction.SCROLL, null, KeyboardController::scrollDown),
    SHIFT_SELECT_RIGHT(KeyType.ArrowRight, TypeAction.SELECT, null, null),
    SHIFT_SELECT_LEFT(KeyType.ArrowLeft, TypeAction.SELECT, null, null),
    ADD(KeyType.Character, TypeAction.ADD, null, KeyboardController::handleCharacter),
    DELETE(KeyType.Backspace, TypeAction.DELETE, null, KeyboardController::deleteCharacter),
    ENTER(KeyType.Enter, TypeAction.ENTER, null, KeyboardController::enter);

    private KeyType keyType;
    private TypeAction typeAction;
    private String keyControl;
    private Runnable function;

    KeyboardAction(final KeyType keyType, final TypeAction typeAction, final String keyControl, final Runnable function) {
        this.keyType = keyType;
        this.typeAction = typeAction;
        this.keyControl = keyControl;
        this.function = function;
    }

    public KeyType getKeyType() { return keyType; }
    public TypeAction getTypeAction() { return typeAction; }
    public String getKeyControl() { return keyControl; }
    public Runnable getFunction() { return function; }

    public int intValue() { return keyControl != null ? keyControl.toCharArray()[0] : -1; }
}
