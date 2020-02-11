package com.dcall.core.app.terminal.constant;

import com.googlecode.lanterna.input.KeyType;

public enum KeyboardAction {
    MOVE_UP(KeyType.ArrowUp),
    MOVE_DOWN(KeyType.ArrowDown),
    MOVE_RIGHT(KeyType.ArrowRight),
    MOVE_LEFT(KeyType.ArrowLeft),
    CTRL_MOVE_RIGHT(KeyType.ArrowRight),
    CTRL_MOVE_LEFT(KeyType.ArrowLeft),
    CTRL_MOVE_START(KeyType.Character),
    CTRL_MOVE_END(KeyType.Character),
    CTRL_CUT(KeyType.Character),
    CTRL_PASTE(KeyType.Character),
    CTRL_CLEAR_SCREEN(KeyType.Character),
    CTRL_EXIT(KeyType.Character),
    SCROLL_UP(KeyType.ArrowUp),
    SCROLL_DOWN(KeyType.ArrowDown),
    SCROLL_TOP(KeyType.PageUp),
    SCROLL_BOTTOM(KeyType.PageDown),
    SHIFT_SELECT_RIGHT(KeyType.ArrowRight),
    SHIFT_SELECT_LEFT(KeyType.ArrowLeft),
    DELETE(KeyType.Backspace),
    ADD(KeyType.Enter);

    private KeyType type;

    KeyboardAction(final KeyType type) {
        this.type = type;
    }

    public KeyType getType() { return type; }
}
