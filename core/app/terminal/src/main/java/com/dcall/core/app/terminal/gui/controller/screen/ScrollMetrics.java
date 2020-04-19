package com.dcall.core.app.terminal.gui.controller.screen;

import com.dcall.core.app.terminal.bus.input.InputEntry;

public final class ScrollMetrics {
    public boolean isInput = true;
    public InputEntry<String> currEntry = null;
    public int inputEntryIdx = 0;
    public int outputEntryIdx = 0;
    public int currBufferIdx = 0;

    public int getEntryIdx() {
        return isInput ? inputEntryIdx : outputEntryIdx;
    }

    public int decrementEntryIdx() {
        if (isInput)
            inputEntryIdx--;
        else
            outputEntryIdx--;

        return getEntryIdx();
    }

    public ScrollMetrics reset() {
        isInput = true;
        currEntry = null;
        inputEntryIdx = 0;
        outputEntryIdx = 0;
        currBufferIdx = 0;

        return this;
    }
}
