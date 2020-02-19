package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.bus.output.InputLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class InputHandler {
    private static final Logger LOG = LoggerFactory.getLogger(InputHandler.class);

    private final List<InputEntry<String> > entries = new ArrayList<>();
    private final ClipBoard clipBoard = new ClipBoard();

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

    public final <T> void cleanEntryFromPos(final InputEntry<T> entry) {
        while (entry.maxNbLine() > entry.posY())
            entry.getBuffer().remove(entry.maxNbLine());
    }

    public final <T> InputLine<T> entryToInputLineFromPos(final InputEntry<T> entry, final InputLine<T> line) {
        int x = entry.posX();
        int y = entry.posY();

        while (y < entry.nbLine()) {
            while (x < entry.getBuffer().get(y).size()) {
                line.add(entry.getBuffer().get(y).getBuffer().get(x));
                entry.getBuffer().get(y).removeAt(x);
            }
            x = 0;
            y++;
        }

        return line;
    }

    public final InputEntry addStrToEntry(final InputEntry<String> entry, final String str) {
        if (str != null) {
            final char[] content = str.toCharArray();
            IntStream.range(0, content.length).forEach(i -> entry.add(String.valueOf(content[i])));
        }

        return entry;
    }

    public final int size() { return entries.size(); }
    public final List<InputEntry<String>> entries() { return entries; }
    public final InputEntry<String> current() { return entries.get(size() - 1); }
    public final ClipBoard clipBoard() { return clipBoard; }
}
