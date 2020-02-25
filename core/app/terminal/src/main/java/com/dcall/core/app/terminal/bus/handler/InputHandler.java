package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.bus.input.InputLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class InputHandler {
    private static final Logger LOG = LoggerFactory.getLogger(InputHandler.class);

    private final List<InputEntry<String> > entries = new ArrayList<>();
    private final ClipBoard clipBoard = new ClipBoard();
    private int entryIdx = 0;
    private InputEntry<String> lastInput = null;

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
        entryIdx = entries.size();

        setLastInput(current());
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

    /** Not optimized for large size. Simply constructs a new entry with current entry and replaces the current with the new. **/
    public final void resizeCurrent() {
        final InputEntry<String> newEntry = new InputEntry<>();

        current().getBuffer().stream()
                .flatMap(line -> line.getBuffer().stream())
                .forEach(e -> newEntry.add(e));

        entries.remove(entries.size() - 1);
        entries.add(newEntry);
    }

    public final InputEntry<String> prevEntry() {
        if (!entries.isEmpty() && entries.size() > 1) {
            entryIdx = (entryIdx <= 0 && entryIdx < entries.size()) ? entries.size() -1 : --entryIdx;
            return entryIdx == 0 ? lastInput : entries.get(entryIdx - 1);
        }

        return null;
    }

    public final InputEntry<String> nextEntry() {
        if (!entries.isEmpty() && entries.size() > 1) {
            entryIdx = (entryIdx >= (entries.size() - 1)) ? -1 : entryIdx;
            if (entryIdx == -1) {
                entryIdx++;
                return lastInput;
            }
            return entries.get(entryIdx++);
        }

        return null;
    }

    public final int size() { return entries.size(); }
    public final List<InputEntry<String>> entries() { return entries; }
    public final InputEntry<String> current() { return entries.get(size() - 1); }
    public final ClipBoard clipBoard() { return clipBoard; }
    public final InputEntry<String> lastInput() { return lastInput; }

    public final void setLastInput(final InputEntry<String> lastInput) {
        this.lastInput = new InputEntry<>();
        this.lastInput.getBuffer().clear();
        this.lastInput.getBuffer().addAll(lastInput.getBuffer());
    }
}
