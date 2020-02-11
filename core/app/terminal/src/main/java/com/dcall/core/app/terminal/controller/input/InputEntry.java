package com.dcall.core.app.terminal.controller.input;

import com.dcall.core.app.terminal.configuration.TermAttributes;
import com.dcall.core.app.terminal.controller.output.InputLine;

import java.util.ArrayList;
import java.util.List;

public class InputEntry<T> {
    private List<InputLine<T>> buffer = null;
    private int x = 0;
    private int y = 0;

    public InputEntry() {
        this.reset();
    }

    public void reset() {
        if (buffer != null)
            System.gc();
        buffer = new ArrayList<>(TermAttributes.DEF_INPUT_NB_LINE);
        x = 0;
        y = 0;
        buffer.add(new InputLine());
    }

    public InputEntry add(final T e) {
        final int currLineSize = buffer.get(y).size();
        final int lineSize = currLineSize + 1;

        if (lineSize > TermAttributes.getTotalLineWidth()) {
            buffer.add(new InputLine());
            y++;
            x = 0;
        }

        buffer.get(y).addAt(x++, e);

        return this;
    }

    public InputEntry remove() {
        final int newX = x - 1;
        final int newY = y - 1;

        if (newX < 0 && newY >= 0) {
            if (newY >= 0)
                this.buffer.remove(y);
            else
                this.buffer.get(y).clear();
            x = TermAttributes.getMaxLineWidth();
            y = newY;
        }
        else
            x = newX >= 0 ? newX : x;

        if (buffer.get(y).size() > 0)
            buffer.get(y).removeAt(x);

        return this;
    }

    public void moveX(final int length) { // 2 | 1 | 5
        final int currLineSize = buffer.get(y).size(); //  78 | 78 | 70
        int newX = x + length; // 77 + 2 = 79 | 77 + 1 = 78 | 70 + 5 = 75

        if (y >= 0) {
            if (newX >= currLineSize) // 79 >= 78 | 78 >= 78 | 75 >= 70
                if (newX > TermAttributes.getMaxLineWidth()) // 79 > 77 | 78 > 77 | 75 > 77
                    newX -= (TermAttributes.getMarginWidth() + 1);  // 79 - 78 = 1 | 78 - 78 = 0 | false
                else
                    newX = currLineSize - 1; // false | false | 70 - 1 = 69;
            else if (newX < 0) {
//                newX = currLineSize - length
            }
        }
    }

    // UTILS
    public int posX() {
        return x;
    }

    public int posY() {
        return y;
    }

    public int nbLine() {
        return buffer.size();
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder(this.buffer.size());

        this.buffer.forEach(l -> sb.append(l.toString()));

        return sb.toString();
    }

    public List<InputLine<T>> getBuffer() { return this.buffer; }
}
