package com.dcall.core.app.terminal.bus.input;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;

import java.util.ArrayList;
import java.util.List;

public class InputLine<T> {
    private List<T> buffer;

    public InputLine() {
        this.buffer = new ArrayList<>(TermAttributes.getTotalLineWidth());
    }

    public InputLine<T> add(final T e) {
        buffer.add(e);

        return this;
    }

    public InputLine<T> addAt(final int i, final T e) {
        buffer.add(i, e);

        return this;
    }

    public InputLine<T> removeAt(final int i) {
        buffer.remove(i);

        return this;
    }

    public InputLine clear() {
        this.buffer.clear();

        return this;
    }

    public List<T> getBuffer() { return buffer; }
    public int size() { return buffer.size(); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.buffer.size());

        this.buffer.forEach(s -> sb.append(s.toString()));

        return sb.toString();
    }

    public void finalize() {
        this.buffer.clear();
        this.buffer = null;
    }
}
