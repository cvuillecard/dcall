package com.dcall.core.app.terminal.bus.input;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.bus.output.InputLine;

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
        buffer = new ArrayList<>(TermAttributes.DEF_INPUT_NB_LINE);
        x = 0;
        y = 0;
        buffer.add(new InputLine<>());
    }

    public InputEntry add(final T e) {
        if (isValidPosition()) {
            if (isAppend()) {
                final int lineSize = currLineSize() + 1;

                if (lineSize > TermAttributes.getTotalLineWidth()) {
                    buffer.add(new InputLine<>());
                    y++;
                    x = 0;
                }

                buffer.get(y).addAt(x++, e);
            }
            else
                return insert(e);
        }

        return this;
    }

    public InputEntry insert(final T e) {
        if (isValidPosition()) {
            if (!isAppend()) {
                final int newTotalSize = totalSize() + 1;
                final int newTotalNbLines = (newTotalSize / TermAttributes.getTotalLineWidth())
                        + ((newTotalSize % TermAttributes.getTotalLineWidth()) > 0 ? 1 : 0);

                if (newTotalNbLines > nbLine())
                    buffer.add(new InputLine<>());

                if (x == TermAttributes.getTotalLineWidth()) {
                    x = 0;
                    y++;
                }

                final List<T> line = buffer.get(y).getBuffer();

                line.add(x++, e);

                if (line.size() > TermAttributes.getTotalLineWidth()) {
                    final int nextY = y + 1;
                    final int endLineIdx = line.size() - 1;
                    final T lastLineElem = line.get(endLineIdx);
                    line.remove(endLineIdx);
                    buffer.get(nextY).addAt(0, lastLineElem);

                    for (int i = nextY; i < nbLine(); i++) {
                        if (buffer.get(i).size() > TermAttributes.getTotalLineWidth()) {
                            final int lastIdx = buffer.get(i).size() - 1;
                            final T lastElem = buffer.get(i).getBuffer().get(lastIdx);
                            buffer.get(i + 1).addAt(0, lastElem);
                            buffer.get(i).removeAt(lastIdx);
                        }
                    }
                }
            } else
                return add(e);
        }

        return this;
    }

    public InputEntry remove() {
        if (isValidPosition()) {
            if (isAppend()) {
                final int newX = x - 1;
                final int newY = y - 1;

                if (newX < 0 && newY >= 0) {
                    this.buffer.remove(y);
                    x = TermAttributes.getMaxLineWidth();
                    y = newY;
                } else
                    x = newX >= 0 ? newX : x;

                if (currLineSize() > 0)
                    buffer.get(y).removeAt(x);
            }
            else
                return delete();
        }

        return this;
    }

    public InputEntry delete() {
        if (isValidPosition()) {
            if (!isAppend()) {
                final int newX = x - 1;
                final int newY = y - 1;

                if (newX < 0 && newY >= 0) {
                    x = TermAttributes.getMaxLineWidth();
                    y = newY;
                }
                else
                    x = newX >= 0 ? newX : x;

                if (currLineSize() > 0) {
                    buffer.get(y).removeAt(x);

                    for (int i = y; nbLine() > 1 && i < maxNbLine(); i++) {
                        final int nextY = i + 1;

                        if (buffer.get(nextY).size() > 0 && buffer.get(i).size() < TermAttributes.getTotalLineWidth()) {
                            final T nextFirst = buffer.get(nextY).getBuffer().get(0);
                            buffer.get(i).addAt(buffer.get(i).size(), nextFirst);
                            buffer.get(nextY).removeAt(0);
                        }
                        else
                            buffer.remove(nextY);
                    }
                }
            }
            else
                return remove();
        }
        return this;
    }

    public void moveAfterX(final int length) {
        if (isValidPosition() && length > 0) {
            final int currLineSize = currLineSize();
            final int newX = x + length;
            final int moved = TermAttributes.getTotalLineWidth() - x;

            if (newX > TermAttributes.getTotalLineWidth()) {
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
        if (isValidPosition() && length < 0) {
            final int newX = x + length;
            final int moved = x;

            if (newX < 0 && y > 0) {
                y--;
                x = TermAttributes.getTotalLineWidth();
                this.moveBeforeX(length + moved);
            } else
                x = newX < 0 && y == 0 ? 0 : newX;
        }
    }

    public void moveX(final int length) {
        if (isValidPosition()) {
            if (length > 0)
                moveAfterX(length);
            else
                moveBeforeX(length);
        }
    }

    public void moveY(final int length) {
        final int newY = y + length;

        if (length > 0)
            y = newY < maxNbLine() ? newY : maxNbLine();
        else
            y = newY >= 0 ? newY : 0;
    }

    public void moveAfter(final T elem) {
        if (isValidPosition() && elem != null) {
            int posY = x > TermAttributes.getMaxLineWidth() && y < maxNbLine() ? y + 1 : y;
            int posX = x > TermAttributes.getMaxLineWidth() && y < maxNbLine() ? 0 : x;
            boolean isOnElem = buffer.get(posY).getBuffer().get(posX).equals(elem);
            boolean found = false;

            while (posY < nbLine()) {
                if (isOnElem)
                    while (posX < buffer.get(posY).size() && buffer.get(posY).getBuffer().get(posX).equals(elem)) posX++;
                else
                    while (posX < buffer.get(posY).size() && !buffer.get(posY).getBuffer().get(posX).equals(elem)) posX++;

                if (posX < buffer.get(posY).size() &&
                        (((isOnElem && !buffer.get(posY).getBuffer().get(posX).equals(elem))
                                || (!isOnElem && buffer.get(posY).getBuffer().get(posX).equals(elem))))) {
                    found = true;
                    break;
                }

                posX = 0;
                posY++;
            }

            setX(found ? posX : buffer.get(maxNbLine()).size());
            setY(found ? posY : maxNbLine());
        }
    }

    public void moveBefore(final T elem) {
        if (isValidPosition() && elem != null) {
            int posY = x == 0 && y > 0 ? y - 1 : y;
            int posX = x >= buffer.get(posY).size() ? buffer.get(posY).size() - 1 : (x > 0 ? x - 1 : TermAttributes.getMaxLineWidth());
            boolean isOnElem =  buffer.get(posY).getBuffer().get(posX).equals(elem);
            boolean found = false;

            while (posY >= 0) {
                if (isOnElem)
                    while (posX > 0 && buffer.get(posY).getBuffer().get(posX).equals(elem)) posX--;
                else
                    while (posX > 0 && !buffer.get(posY).getBuffer().get(posX).equals(elem)) posX--;

                if (posX < buffer.get(posY).size() &&
                        (((isOnElem && !buffer.get(posY).getBuffer().get(posX).equals(elem))
                                || (!isOnElem && buffer.get(posY).getBuffer().get(posX).equals(elem))))) {
                    found = true;
                    break;
                }

                posX = TermAttributes.getMaxLineWidth();
                posY--;
            }

            setX(found ? (posX == TermAttributes.getMaxLineWidth() ? 0 : posX + 1) : 0);
            setY(found ? (posX == TermAttributes.getMaxLineWidth() ? posY + 1 : posY) : 0);
//            setX(found ? (posX == TermAttributes.getMaxLineWidth() ? posX : posX + 1) : 0);
//            setY(found ? (posX == TermAttributes.getMaxLineWidth() && posY > 0 ? posY -1 : posY) : 0);
        }
    }

    // GETTERS
    public List<InputLine<T>> getBuffer() { return this.buffer; }
    public int posX() { return this.x; }
    public int posY() { return this.y; }

    // SETTERS
    public void setX(final int posX) { this.x = posX; }
    public void setY(final int posY) { this.y = posY; }

    // UTILS
    public InputLine<T> current() { return this.buffer.get(y); }
    public int nbLine() { return this.buffer.size(); }
    public int maxNbLine() { return this.nbLine() - 1; }
    public boolean isValidPosition() { return this.x >= 0 && this.y <= maxNbLine() && this.y >= 0 && x <= buffer.get(y).size(); }
    public boolean isAppend() { return y == maxNbLine() && x == buffer.get(y).size(); }
    public int totalSize() { return this.buffer.stream().mapToInt(b -> b.getBuffer().size()).sum(); }
    public int currLineSize() {   return buffer.get(y).size(); }

    public String toString() {
        final StringBuilder sb = new StringBuilder(this.buffer.size());

        this.buffer.forEach(l -> sb.append(l.toString()));

        return sb.toString();
    }

    public void finalize() {
        this.buffer.clear();
        this.buffer = null;
    }
}
