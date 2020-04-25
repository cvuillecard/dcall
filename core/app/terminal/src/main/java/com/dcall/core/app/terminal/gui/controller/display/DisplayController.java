package com.dcall.core.app.terminal.gui.controller.display;

import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.bus.input.InputLine;
import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.CursorController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.controller.screen.ScrollMetrics;
import com.dcall.core.app.terminal.gui.service.drawer.TextDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.*;

public final class DisplayController {
    private static final Logger LOG = LoggerFactory.getLogger(DisplayController.class);
    private static volatile boolean lock = false;
    private static volatile boolean dataReady = true;

    public static void init(final ScreenMetrics metrics) {
        TextDrawer.drawHeader(metrics.width);
        metrics.currX++;
    }

    public static void displayPrompt(final ScreenMetrics metrics) {
        TextDrawer.drawHeader(TermAttributes.FRAME_NB_COLS);
        TextDrawer.drawPrompt(metrics);

        metrics.currX = TermAttributes.getPromptStartIdx();

        CursorController.moveAt(metrics);

        ScreenController.refresh();
    }

    public static void displayCharacter(final InputEntry<String> entry, final ScreenMetrics metrics, final String character) {
        LOG.debug("character : " + character);

        TextDrawer.drawInputString(metrics.currX++, metrics.currY, character);

        if (metrics.currX == metrics.maxX) {
            metrics.currX = metrics.minX;
            metrics.currY++;
        }

        if (!entry.isAppend())
            drawInputEntry(entry, metrics);

        CursorController.moveAt(metrics);

        ScreenController.refresh();
    }

    public static void deleteCharacter(final InputEntry<String> entry, final ScreenMetrics metrics) {

        if (metrics.currX == metrics.minX) {
            metrics.currX = getTotalLineWidth();
            metrics.currY--;
        }
        else
            metrics.currX--;

        TextDrawer.drawCharacter(metrics.currX, metrics.currY, ' ');

        if (!entry.isAppend()) {
            drawInputEntry(entry, metrics);
            final InputLine<String> lastLine = entry.getBuffer().get((entry.maxNbLine()));
            TextDrawer.drawCharacter(metrics.screenPosX(lastLine.getBuffer().size()), metrics.screenPosY(entry.maxNbLine()), ' ');
        }

        CursorController.moveAt(metrics);

        ScreenController.refresh();
    }

    private static void drawInputEntry(final InputEntry<String> entry, final ScreenMetrics metrics) {
        TextDrawer.drawInputString(metrics.screenPosX(entry.posX()), metrics.screenPosY(entry.posY()),
                entry.current().toString().substring(entry.posX(), entry.current().size()));

        if (entry.posY() < entry.maxNbLine()) {
            IntStream.range(entry.posY() + 1, entry.nbLine()).forEach(y ->
                    TextDrawer.drawInputString(metrics.screenPosX(0), metrics.screenPosY(y), entry.getBuffer().get(y).toString())
            );
        }
    }

    public static void drawOutputEntry(final IOHandler bus, final ScreenMetrics metrics) {
        final InputEntry<String> entry = bus.output().current();

        if (!entry.isAppend()) {
            for (int y = bus.output().getLastIdx(), i = 0; y < entry.nbLine(); y++) {
                if ((metrics.currY + 1) > metrics.maxY - 1) {
                    ScreenController.getScreen().scrollLines(TermAttributes.MARGIN_TOP, metrics.maxY, TermAttributes.DEFAULT_SCROLL_PADDING);
                    metrics.minY -= TermAttributes.DEFAULT_SCROLL_PADDING;
//                    ScreenController.refresh();
                }
                metrics.currY = metrics.screenPosY(i++);
                TextDrawer.drawOutputString(metrics.screenPosX(0), metrics.currY, entry.getBuffer().get(y).toString());
                bus.output().setLastIdx(y + 1);
            }

            metrics.currY++;
            metrics.minY = metrics.currY;
            moveAt(metrics);
        }
    }

    public static void displayPromptOnReady(final IOHandler bus) {
        if (dataReady) {
            lock = false;
            addPrompt(bus);
        }
    }

    public static void moveAt(final ScreenMetrics metrics) {
        TextDrawer.drawHeader(TermAttributes.FRAME_NB_COLS);
        CursorController.moveAt(metrics);
        ScreenController.refresh();
    }

    public static void drawBlankFromPos(final InputEntry<String> entry, final ScreenMetrics metrics) {
        final int nextY = entry.posY() + 1;
        // Lanterna : TextGraphics.drawLine() doesn't handle linear drawing from a start position to an end position with a different Y value -> anyway seems to be bugged or not complete
        // Only startY seems to be considered by drawLine, so we can only draw the current rest of line from current position
        TextDrawer.drawBlank(metrics.screenPosX(entry.posX()), metrics.screenPosY(entry.posY()), metrics.screenPosX(entry.current().size()), metrics.screenPosY(entry.posY()));
        // because of drawLing incapacity and drawRectangle bugging if more than 3 lines to draw, i have not the choice of only print line by line...sorry, or i have to code a graphical library using swing
        IntStream.range(nextY, entry.nbLine()).forEach(y -> TextDrawer.drawBlank(metrics.screenPosX(0), metrics.screenPosY(y), metrics.screenPosX(entry.getBuffer().get(y).size() - 1), metrics.screenPosY(y)));
    }

    public static void drawBlankEntry(final InputEntry<String> entry, final ScreenMetrics metrics) {
        final int nextY = entry.posY() + 1;
        TextDrawer.drawBlank(metrics.screenPosX(entry.posX()), metrics.screenPosY(entry.posY()), metrics.screenPosX(entry.current().size()), metrics.screenPosY(entry.posY()));
        IntStream.range(nextY, TermAttributes.FRAME_NB_ROWS).forEach(y -> TextDrawer.drawBlank(metrics.screenPosX(0), metrics.screenPosY(y), metrics.maxX, metrics.screenPosY(y)));
    }

    public static void cut(final IOHandler bus, final ScreenMetrics metrics) {
        final InputEntry<String> entry = bus.input().current();
        final InputLine<String> input = new InputLine<>();

        drawBlankFromPos(entry, metrics);

        bus.input().entryToInputLineFromPos(entry, input);
        bus.input().cleanEntryFromPos(entry);
        bus.input().clipBoard().setContent(input.toString());

        ScreenController.refresh();
    }

    public static void drawInputEntryFromPos(final InputEntry<String> entry, final ScreenMetrics metrics) {
        int nextY = metrics.posY() + 1;

        drawCurrentInputLine(entry, metrics);

        IntStream.range(nextY, entry.nbLine()).forEach(y -> TextDrawer.drawInputString(metrics.screenPosX(0), metrics.screenPosY(y), entry.getBuffer().get(y).toString()));
    }

    public static void drawCurrentInputLine(final InputEntry<String> entry, final ScreenMetrics metrics) {
        if (metrics.posY() == 0 && metrics.posX() < TermAttributes.getPrompt().length()) {
            TextDrawer.drawPrompt(metrics);
            metrics.currX = metrics.screenPosX(TermAttributes.getPrompt().length());
        }

        if (metrics.posY() <= entry.maxNbLine())
            TextDrawer.drawInputString(metrics.currX, metrics.currY, entry.getBuffer().get(metrics.posY()).toString().substring(metrics.posX()));
    }

    public static void paste(final InputEntry<String> entry, final int length, final ScreenMetrics metrics) {
        updateScreenMetrics(entry, metrics);

        entry.setX(metrics.posX());
        entry.setY(metrics.posY());

        entry.moveX(length);

        metrics.currX = metrics.screenPosX(entry.posX());
        metrics.currY = metrics.screenPosY(entry.posY());

        moveAt(metrics);
    }

    public static void clearScreen(final IOHandler bus, final ScreenMetrics metrics, final ScrollMetrics scrollMetrics) {
        if (metrics.minY > MARGIN_TOP) {
            final InputEntry<String> entry = bus.input().current();
            final int posX = metrics.posX();
            final int posY = metrics.posY();
            final int distance = metrics.minY - MARGIN_TOP;
            ScreenController.getScreen().scrollLines(MARGIN_TOP, metrics.maxY, distance);
            ScreenController.refresh();

            entry.setX(0);
            entry.setY(0);

            metrics.minY = MARGIN_TOP;
            metrics.currY = MARGIN_TOP;
            metrics.currX = MARGIN_LEFT;

            DisplayController.drawInputEntryFromPos(entry, metrics);

            entry.setX(posX);
            entry.setY(posY);
            metrics.currX = metrics.screenPosX(entry.posX());
            metrics.currY = metrics.screenPosY(entry.posY());

            scrollMetrics.reset();

            moveAt(metrics);
        }
    }

    public static void updateScreenMetrics(final InputEntry<String> entry, final ScreenMetrics metrics) {
        int nextY = metrics.screenPosY(entry.posY());
        final int posX = metrics.posX();
        final int posY = metrics.posY();

        if (nextY > metrics.maxY) {
            final int distance = nextY - metrics.maxY;
            metrics.minY-= distance;
            metrics.currX = metrics.screenPosX(posX);
            metrics.currY = metrics.screenPosY(posY);
            ScreenController.getScreen().scrollLines(MARGIN_TOP, metrics.maxY, distance);
            ScreenController.refresh();
            drawBlankFromPos(entry, metrics);
            drawInputEntryFromPos(entry, metrics);
        }
        else if (nextY < MARGIN_TOP) {
            if (entry.posY() == 0) {
                final int entryPosX = entry.posX();
                entry.setX(0);
                entry.setY(0);
                metrics.minY = MARGIN_TOP;
                metrics.currX = metrics.screenPosX(entry.posX());
                metrics.currY =  metrics.screenPosY(entry.posY());
                drawBlankFromPos(entry, metrics);
                entry.setX(TermAttributes.getPrompt().length());
                drawInputEntryFromPos(entry, metrics);
                entry.setX(entryPosX);
                metrics.currX = metrics.screenPosX(entryPosX);
            }
            else {
                final int nbLine = metrics.minY < 0 ? (metrics.minY * -1) : metrics.minY;
                final int distance = (nbLine + MARGIN_TOP) - entry.posY();
                metrics.minY += distance;
                ScreenController.getScreen().scrollLines(MARGIN_TOP, metrics.maxY, distance * -1);
                ScreenController.refresh();
                metrics.currX = metrics.screenPosX(0);
                metrics.currY =  metrics.screenPosY(entry.posY());
                drawInputEntryFromPos(entry, metrics);
                metrics.currX = metrics.screenPosX(entry.posX());
            }
        }
        else {
//            drawBlankFromPos(entry, metrics);
            drawInputEntryFromPos(entry, metrics);
        }
    }

    public static void resize(final ScreenMetrics metrics) {
        if (metrics.width != TermAttributes.DEF_FRAME_NB_COLS || metrics.height != TermAttributes.DEF_FRAME_NB_ROWS) {
            GUIProcessor.resize(metrics);
            moveAt(metrics);
        }
    }

    /**
     * Method used to lock DisplayController when bus (IOHandler) is again waiting datas from last input executed
     */
    public static void lock() {
        lock = true;
        dataReady = false;
    }

    /**
     * Method used to unlock DisplayController when all bus's output datas are available
     */
    public static void unlock() {
        lock = false;
    }

    public static boolean getLock() {
        return lock;
    }

    public static void setDataReady(final boolean ready) {
        dataReady = ready;
    }

    public static boolean dataReady() {
        return dataReady;
    }

    public static void addPrompt(final IOHandler bus) {
        final ScreenMetrics metrics = ScreenController.metrics();

        bus.input().addEntry(TermAttributes.getPrompt());

        CursorController.moveAt(metrics);
        DisplayController.displayPrompt(metrics);
    }


    /** COMMON SCROLL **/
    private static int drawScrollEntryRange(ScreenMetrics metrics, ScrollMetrics scrollMetrics, int i, int y, String line, int x) {
        if (scrollMetrics.isInput) {
            if (i == 0) {
                final int lastY = metrics.currY;
                metrics.currY = y;
                TextDrawer.drawPrompt(metrics);
                metrics.currY = lastY;
                x += TermAttributes.getPrompt().length();
                line = line.substring(TermAttributes.getPrompt().length());
            }
            TextDrawer.drawInputString(x, y++, line);
        }
        else
            TextDrawer.drawOutputString(x, y++, line);
        return y;
    }

    /** SCROLL DOWN **/
    public static void scrollDown(final IOHandler bus, final ScreenMetrics metrics, final ScrollMetrics scrollMetrics, int scrollPadding) {
        if (scrollMetrics.currEntry != null && scrollPadding > 0) {

//            initScrollDownMetrics(bus, scrollMetrics.accu);

            final int currBuffSize = scrollMetrics.accu.currEntry.getBuffer().size();
            final int newBuffIdx = scrollMetrics.accu.currBufferIdx + scrollPadding;
            int padding = newBuffIdx > currBuffSize ? currBuffSize - scrollMetrics.accu.currBufferIdx : scrollPadding;

            ScreenController.getScreen().scrollLines(MARGIN_TOP, metrics.maxY, padding);

            metrics.minY -= padding;
            metrics.currY -= padding;

            drawDownEntryRange(metrics, scrollMetrics.accu, (scrollMetrics.accu.currBufferIdx + padding), padding);

            scrollCursor(metrics);

            ScreenController.refresh();

            updateScrollMetrics(bus, scrollMetrics, padding);

            if (!isBottomEndScroll(bus, scrollMetrics)) {
                updateScrollMetrics(bus, scrollMetrics.accu, padding);
                if (padding < scrollPadding) {
                    scrollDown(bus, metrics, scrollMetrics, scrollPadding - padding);
                }
            }
        }
    }

    private static boolean isBottomEndScroll(final IOHandler bus, final ScrollMetrics scrollMetrics) {
        return scrollMetrics.accu.currEntry.equals(bus.input().current()) && scrollMetrics.accu.currBufferIdx >= (bus.input().current().getBuffer().size() - 1);
    }

    private static void updateScrollMetrics(final IOHandler bus, final ScrollMetrics scrollMetrics, int scrollPadding) {
        scrollMetrics.currBufferIdx += scrollPadding;

        if (scrollMetrics.currBufferIdx >= scrollMetrics.currEntry.getBuffer().size()) {
            int rest = scrollMetrics.currBufferIdx - scrollMetrics.currEntry.getBuffer().size();
            if (!scrollMetrics.isInput) {
                incrementScrollMetrics(bus, scrollMetrics);
                scrollMetrics.outputEntryIdx =  scrollMetrics.inputEntryIdx > scrollMetrics.outputEntryIdx ? scrollMetrics.outputEntryIdx + 1 : scrollMetrics.outputEntryIdx;
            }
            else {
                scrollMetrics.isInput = !scrollMetrics.isInput;
                scrollMetrics.currEntry = bus.entries(scrollMetrics.isInput).get(scrollMetrics.getEntryIdx());
            }
            scrollMetrics.currBufferIdx = rest;
            if (scrollMetrics.currBufferIdx >= scrollMetrics.currEntry.getBuffer().size()) {
//                rest = scrollMetrics.currBufferIdx - scrollMetrics.currEntry.getBuffer().size();
                scrollMetrics.currBufferIdx = 0;
//                incrementScrollMetrics(bus, scrollMetrics);
                updateScrollMetrics(bus, scrollMetrics, rest);
            }
        }
    }

//    private static void updateScrollMetrics(final IOHandler bus, final ScrollMetrics scrollMetrics, int scrollPadding) {
//        scrollMetrics.currBufferIdx += scrollPadding;
//
//        if (scrollMetrics.currBufferIdx >= scrollMetrics.currEntry.getBuffer().size()) {
//            final int padding = scrollMetrics.currBufferIdx - scrollMetrics.currEntry.getBuffer().size();
//                scrollMetrics.isInput = !scrollMetrics.isInput;
//            if (scrollMetrics.getEntryIdx() + 1 < bus.entries(scrollMetrics.isInput).size()) {
//                scrollMetrics.incrementEntryIdx();
//                scrollMetrics.currEntry = bus.entries(scrollMetrics.isInput).get(scrollMetrics.getEntryIdx());
//                scrollMetrics.currBufferIdx = padding;
//                if (padding >= scrollMetrics.currEntry.getBuffer().size()) {
//                    scrollMetrics.currBufferIdx = 0;
//                    updateScrollMetrics(bus, scrollMetrics, padding - scrollMetrics.currEntry.getBuffer().size());
//                }
//            }
//        }
//    }

    private static void drawDownEntryRange(final ScreenMetrics metrics, final ScrollMetrics scrollMetrics, final int newBuffIdx, final int padding) {
        for (int i = scrollMetrics.currBufferIdx, y = metrics.height - padding; i < newBuffIdx; i++)
            y = drawScrollEntryRange(metrics, scrollMetrics, i, y, scrollMetrics.currEntry.getBuffer().get(i).toString(), TermAttributes.MARGIN_LEFT);
    }

    private static void initScrollDownMetrics(final IOHandler bus, final ScrollMetrics scrollMetrics) {
        if (scrollMetrics.currBufferIdx < 0) {
            scrollMetrics.isInput = !scrollMetrics.isInput;
            if (scrollMetrics.getEntryIdx() - 1 >= 0) {
                scrollMetrics.decrementEntryIdx();
                scrollMetrics.currEntry = bus.entries(scrollMetrics.isInput).get(scrollMetrics.getEntryIdx());
                scrollMetrics.currBufferIdx = scrollMetrics.currEntry.getBuffer().size() + scrollMetrics.currBufferIdx;
                if (scrollMetrics.currBufferIdx < 0)
                    initScrollDownMetrics(bus, scrollMetrics);
            }
        }
    }

    /** SCROLL UP **/
    public static void scrollUp(final IOHandler bus, final ScreenMetrics metrics, final ScrollMetrics scrollMetrics, int scrollPadding) {
        initScrollMetrics(bus, metrics, scrollMetrics);

        final int newBuffIdx = scrollMetrics.currBufferIdx - scrollPadding;
        scrollPadding = newBuffIdx >= 0 ? scrollPadding : scrollPadding + newBuffIdx;

        metrics.minY += scrollPadding;
        metrics.currY += scrollPadding;

        ScreenController.getScreen().scrollLines(MARGIN_TOP, metrics.maxY, scrollPadding * -1);

        drawUpEntryRange(metrics, scrollMetrics, newBuffIdx);

        scrollMetrics.accu.currBufferIdx = scrollMetrics.accu.currBufferIdx - scrollPadding;
        initScrollDownMetrics(bus, scrollMetrics.accu);

        scrollCursor(metrics);

        ScreenController.refresh();

        updateScrollUpMetrics(bus, metrics, scrollMetrics, newBuffIdx);
//        updateScrollUpMetrics(bus, metrics, scrollMetrics, scrollPadding);
    }

//    private static void updateScrollUpMetrics(final IOHandler bus, final ScreenMetrics metrics, final ScrollMetrics scrollMetrics, final int scrollPadding) {
//        scrollMetrics.currBufferIdx -= scrollPadding;
//
//        if (scrollMetrics.currBufferIdx < 0 && scrollMetrics.inputEntryIdx > 0) {
//            int rest = scrollMetrics.currBufferIdx * -1;
//
//            if (scrollMetrics.isInput) {
//                derementScrollMetrics(bus, scrollMetrics);
//                scrollMetrics.inputEntryIdx =  scrollMetrics.outputEntryIdx;
//            }
//            else {
//                scrollMetrics.isInput = !scrollMetrics.isInput;
//                scrollMetrics.currEntry = bus.entries(scrollMetrics.isInput).get(scrollMetrics.getEntryIdx());
//                scrollMetrics.currBufferIdx = scrollMetrics.currEntry.getBuffer().size();
//            }
//
//            scrollMetrics.currBufferIdx -= rest;
//
//            if (scrollMetrics.currBufferIdx < 0) {
//                scrollMetrics.currBufferIdx = scrollMetrics.currEntry.getBuffer().size();
//                scrollUp(bus, metrics, scrollMetrics, rest * -1);
//            }
//        }
//    }

    private static void updateScrollUpMetrics(final IOHandler bus, final ScreenMetrics metrics, final ScrollMetrics scrollMetrics, final int newBuffIdx) {
        scrollMetrics.currBufferIdx = newBuffIdx;

        if (scrollMetrics.currBufferIdx < 0 && scrollMetrics.inputEntryIdx > 0) {
            scrollMetrics.isInput = !scrollMetrics.isInput;

            if (scrollMetrics.getEntryIdx() - 1 >= 0) {
                scrollMetrics.decrementEntryIdx();
                scrollMetrics.currEntry = bus.entries(scrollMetrics.isInput).get(scrollMetrics.getEntryIdx());
                scrollMetrics.currBufferIdx = scrollMetrics.currEntry.getBuffer().size();
                if (newBuffIdx < 0)
                    scrollUp(bus, metrics, scrollMetrics, newBuffIdx * -1);
            }
        }
    }

    private static void scrollCursor(final ScreenMetrics metrics) {
        if (metrics.currY > metrics.maxY)
            ScreenController.hideCursor();
        else
            CursorController.moveAt(metrics);
    }

    private static void drawUpEntryRange(final ScreenMetrics metrics, final ScrollMetrics scrollMetrics, int newBuffIdx) {
        for (int i = newBuffIdx < 0 ? 0 : newBuffIdx, y = MARGIN_TOP; i < scrollMetrics.currBufferIdx; i++)
            y = drawScrollEntryRange(metrics, scrollMetrics, i, y, scrollMetrics.currEntry.getBuffer().get(i).toString(), TermAttributes.MARGIN_LEFT);
    }

    private static void initScrollMetrics(final IOHandler bus, final ScreenMetrics metrics, final ScrollMetrics scrollMetrics) {
        if (scrollMetrics.currEntry == null || isBottomEndScroll(bus, scrollMetrics)) {
            final int topDistance = metrics.minY - MARGIN_TOP;
            final int bottomDistance = metrics.minY - metrics.height;

            scrollMetrics.isInput = false;
            scrollMetrics.currEntry = bus.output().current();
            scrollMetrics.inputEntryIdx = bus.input().size() - 1;
            scrollMetrics.outputEntryIdx = bus.output().size() - 1;
            scrollMetrics.currBufferIdx = scrollMetrics.currEntry.getBuffer().size() - topDistance;

            scrollMetrics.accu = new ScrollMetrics(scrollMetrics);
            scrollMetrics.accu.inputEntryIdx -= 1;
            scrollMetrics.accu.currBufferIdx = scrollMetrics.currEntry.getBuffer().size() - bottomDistance;
        }
    }

    // SCROLL UTILS
    private static void incrementScrollMetrics(final IOHandler bus, final ScrollMetrics scrollMetrics) {
        scrollMetrics.isInput = !scrollMetrics.isInput;

        if (scrollMetrics.getEntryIdx() + 1 < bus.entries(scrollMetrics.isInput).size()) {
            scrollMetrics.incrementEntryIdx();
            scrollMetrics.currEntry = bus.entries(scrollMetrics.isInput).get(scrollMetrics.getEntryIdx());
            scrollMetrics.currBufferIdx = 0;
        }
    }

    private static void derementScrollMetrics(final IOHandler bus, final ScrollMetrics scrollMetrics) {
        scrollMetrics.isInput = !scrollMetrics.isInput;

        if (scrollMetrics.getEntryIdx() - 1 >= 0) {
            scrollMetrics.decrementEntryIdx();
            scrollMetrics.currEntry = bus.entries(scrollMetrics.isInput).get(scrollMetrics.getEntryIdx());
            scrollMetrics.currBufferIdx = scrollMetrics.currEntry.getBuffer().size();
        }
    }
}
