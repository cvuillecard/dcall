package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.bus.input.InputLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.getNbLines;
import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.getTotalLineWidth;

public class OutputHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OutputHandler.class);

    private final List<InputEntry<String>> entries = new ArrayList<>();
    private int lastIdx = 0;

    public final void reset() {
        this.entries.clear();
        this.lastIdx = 0;

//        System.gc();
    }

    public final void clear() {
        this.current().reset();
    }

    public final OutputHandler addEntry() {
        entries.add(new InputEntry<>());
        this.lastIdx = 0;

        current().getBuffer().clear();

        return this;
    }

    public void addToEntry(final String str) {
        final String[] lines = str.split("\n");
        IntStream.range(0, lines.length).forEach(i -> {
            if (lines[i].length() > getTotalLineWidth()) {
                IntStream.range(0, getNbLines(lines[i].length()))
                        .forEach(idx -> {
                            final int startIdx = idx * getTotalLineWidth();
                            final int endIdx = startIdx + getTotalLineWidth();
                            final InputLine<String> inputLine = new InputLine<String>()
                                    .add(lines[i].substring(startIdx, endIdx > lines[i].length() ? lines[i].length() : endIdx));
                            current().getBuffer().add(inputLine);
                        });
            }
            else
                current().getBuffer().add(new InputLine<String>().add(lines[i]));
        });
    }

    public final int size() { return entries.size(); }
    public final List<InputEntry<String>> entries() { return entries; }
    public final InputEntry<String> current() { return entries.get(size() - 1); }
    public int getLastIdx() { return this.lastIdx; }
    public void setLastIdx(final int idx) { this.lastIdx = idx; }

}
