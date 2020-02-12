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
        final int currLineSize = currLineSize();
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

        if (currLineSize() > 0)
            buffer.get(y).removeAt(x);

        return this;
    }


    public void moveAfterX(final int length) {
        if (length > 0) {
            final int currLineSize = currLineSize();
            final int newX = x + length;
            final int moved = TermAttributes.getTotalLineWidth() - x;

            if (newX > TermAttributes.getMaxLineWidth()) {
                if (y < this.maxNbLine()) {
                    y++;
                    x = 0;
                }
                this.moveAfterX(length - moved);
            } else
                x = newX > currLineSize ? currLineSize : newX;
        }
    }

    public void moveBeforeX(final int length) {
        if (length < 0) {
            final int newX = x + length;
            final int moved = x;

            if (newX < 0 && y > 0) {
                y--;
                x = TermAttributes.getMaxLineWidth();
                this.moveBeforeX(length + moved);
            } else
                x = newX < 0 && y == 0 ? 0 : newX;
        }
    }

    public void moveX(final int length) {
        if (length > 0)
            moveAfterX(length);
        else
            moveBeforeX(length);
    }

    public void moveY(final int length) {
        final int newY = y + length;

        if (length > 0)
            y = newY < maxNbLine() ? newY : maxNbLine();
        else
            y = newY >= 0 ? newY : 0;
    }

    // GETTERS
    public List<InputLine<T>> getBuffer() { return this.buffer; }
    public int posX() { return this.x; }
    public int posY() { return this.y; }

    // SETTERS
    public void setX(final int posX) { this.x = posX; }
    public void setY(final int posY) { this.y = posY; }

    // UTILS
    public int nbLine() { return this.buffer.size(); }
    public int maxNbLine() { return this.nbLine() - 1; }
    private int currLineSize() {   return buffer.get(y).size(); }

    public String toString() {
        final StringBuilder sb = new StringBuilder(this.buffer.size());

        this.buffer.forEach(l -> sb.append(l.toString()));

        return sb.toString();
    }

}
