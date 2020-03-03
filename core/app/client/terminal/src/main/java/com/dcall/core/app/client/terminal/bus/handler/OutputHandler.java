package com.dcall.core.app.client.terminal.bus.handler;

import com.dcall.core.app.client.terminal.bus.input.InputLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.dcall.core.app.client.terminal.gui.configuration.TermAttributes.getNbLines;
import static com.dcall.core.app.client.terminal.gui.configuration.TermAttributes.getTotalLineWidth;

public class OutputHandler {
    private static final Logger LOG = LoggerFactory.getLogger(OutputHandler.class);

    private final List<InputLine<String>> entries = new ArrayList<>();


    public final int size() { return entries.size(); }
    public final List<InputLine<String>> entries() { return entries; }
    public final InputLine<String> current() { return entries.get(size() - 1); }

    public void addInputLine(final String str) {
        final String[] lines = str.split("\n");
        IntStream.range(0, lines.length).forEach(i -> {
            if (lines[i].length() > getTotalLineWidth()) {
                IntStream.range(0, getNbLines(lines[i].length()))
                        .forEach(idx -> {
                            final int startIdx = idx * getTotalLineWidth();
                            final int endIdx = startIdx + getTotalLineWidth();
                            current().add(lines[i].substring(startIdx, endIdx > lines[i].length() ? lines[i].length() : endIdx));
                        });
            }
            else
                current().add(lines[i]);
        });
    }
}
