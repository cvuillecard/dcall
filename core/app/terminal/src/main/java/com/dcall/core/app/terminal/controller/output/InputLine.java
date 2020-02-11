package com.dcall.core.app.terminal.controller.output;

import com.dcall.core.app.terminal.configuration.TermAttributes;

import java.util.ArrayList;
import java.util.List;

public class InputLine<T> {
    private List<T> buffer;

    public InputLine() {
        this.buffer = new ArrayList<>(TermAttributes.getMaxLineWidth() + 1);
    }

    public InputLine add(final T e) {
        buffer.add(e);

        return this;
    }

    public InputLine addAt(final int i, final T e) {
        buffer.add(i, e);

        return this;
    }

    public InputLine removeAt(final int i) {
        buffer.remove(i);

        return this;
    }

    public InputLine clear() {
        this.buffer.clear();

        return this;
    }

    public void finalize() {
        this.buffer.clear();
        this.buffer = null;
    }

    public List<T> getBuffer() { return buffer; }
    public int size() { return buffer.size(); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.buffer.size());

        this.buffer.forEach(s -> sb.append(s.toString()));

        return sb.toString();
    }
}
