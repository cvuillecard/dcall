package com.dcall.core.app.terminal.gui.controller.keyboard;

import com.googlecode.lanterna.input.KeyType;

public enum KeyboardAction {
    MOVE_UP(KeyType.ArrowUp, TypeAction.MOVE, null, null),
    MOVE_DOWN(KeyType.ArrowDown, TypeAction.MOVE, null, null),
    MOVE_RIGHT(KeyType.ArrowRight, TypeAction.MOVE, null, null),
    MOVE_LEFT(KeyType.ArrowLeft, TypeAction.MOVE, null, KeyboardController::moveLeft),
    CTRL_MOVE_RIGHT(KeyType.ArrowRight, TypeAction.CTRL, null, null),
    CTRL_MOVE_LEFT(KeyType.ArrowLeft, TypeAction.CTRL, null, null),
    CTRL_MOVE_START(KeyType.Character, TypeAction.CTRL, "A", KeyboardController::moveStart),
    CTRL_MOVE_END(KeyType.Character, TypeAction.CTRL, "E", KeyboardController::moveEnd),
    CTRL_CUT(KeyType.Character, TypeAction.CTRL, "K", null),
    CTRL_PASTE(KeyType.Character, TypeAction.CTRL, "Y", null),
    CTRL_CLEAR_SCREEN(KeyType.Character, TypeAction.CTRL, "L", null),
    CTRL_EXIT(KeyType.Character, TypeAction.CTRL, "C", KeyboardController::stop),
    SCROLL_UP(KeyType.ArrowUp, TypeAction.SCROLL, null, null),
    SCROLL_DOWN(KeyType.ArrowDown, TypeAction.SCROLL, null, null),
    SCROLL_TOP(KeyType.PageUp, TypeAction.SCROLL, null, null),
    SCROLL_BOTTOM(KeyType.PageDown, TypeAction.SCROLL, null, null),
    SHIFT_SELECT_RIGHT(KeyType.ArrowRight, TypeAction.SELECT, null, null),
    SHIFT_SELECT_LEFT(KeyType.ArrowLeft, TypeAction.SELECT, null, null),
    ADD(KeyType.Character, TypeAction.ADD, null, KeyboardController::handleCharacter),
    DELETE(KeyType.Backspace, TypeAction.DELETE, null, KeyboardController::deleteCharacter),
    ENTER(KeyType.Enter, TypeAction.ENTER, null, null);

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

    public int intValue() { return keyControl != null ? (int)keyControl.toCharArray()[0] : -1; }
}
