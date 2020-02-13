package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.bus.input.InputEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class InputHandler {
    private final List<InputEntry<String> > entries = new ArrayList<>();

    public final void reset() {
        this.entries.clear();
        System.gc();
    }

    public final void clear() {
        this.current().reset();
    }

    public final void addEntry(final String input) {
        final char[] in = input.toCharArray();
        final InputEntry<String> entry = new InputEntry<>();

        IntStream.range(0, in.length).forEach(i -> entry.add(String.valueOf(in[i])));

        entries.add(entry);
    }

    public final int size() { return entries.size(); }
    public final List<InputEntry<String>> entries() { return entries; }
    public final InputEntry<String> current() { return entries.get(size() - 1); }
}
