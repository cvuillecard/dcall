package com.dcall.core.app.terminal.gui.service.scroll;

import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.screen.CursorController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.controller.screen.ScrollMetrics;
import com.dcall.core.app.terminal.gui.service.drawer.TextDrawerService;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.MARGIN_TOP;

public final class ScrollService {

    /** COMMON SCROLL **/
    private static int drawScrollEntryRange(ScreenMetrics metrics, ScrollMetrics scrollMetrics, int i, int y, String line, int x) {
        if (scrollMetrics.isInput) {
            if (i == 0) {
                final int lastY = metrics.currY;
                metrics.currY = y;
                TextDrawerService.drawPrompt(metrics);
                metrics.currY = lastY;
                x += TermAttributes.getPrompt().length();
                line = line.substring(TermAttributes.getPrompt().length());
            }
            TextDrawerService.drawInputString(x, y++, line);
        }
        else
            TextDrawerService.drawOutputString(x, y++, line);
        return y;
    }

    private static void incrementScrollMetrics(final IOHandler bus, final ScrollMetrics scrollMetrics) {
        scrollMetrics.isInput = !scrollMetrics.isInput;

        if (scrollMetrics.getEntryIdx() + 1 < bus.entries(scrollMetrics.isInput).size()) {
            scrollMetrics.incrementEntryIdx();
            scrollMetrics.currEntry = bus.entries(scrollMetrics.isInput).get(scrollMetrics.getEntryIdx());
            scrollMetrics.currBufferIdx = 0;
        }
    }

    private static boolean isBottomEndScroll(final IOHandler bus, final ScrollMetrics scrollMetrics) {
        return scrollMetrics.accu.currEntry.equals(bus.input().current()) && scrollMetrics.accu.currBufferIdx >= (bus.input().current().getBuffer().size() - 1);
    }

    /** SCROLL DOWN **/
    public static void scrollDown(final IOHandler bus, final ScreenMetrics metrics, final ScrollMetrics scrollMetrics, int scrollPadding) {
        if (scrollMetrics.currEntry != null && scrollPadding > 0) {
            final int currBuffSize = scrollMetrics.accu.currEntry.getBuffer().size();
            final int newBuffIdx = scrollMetrics.accu.currBufferIdx + scrollPadding;
            int padding = newBuffIdx > currBuffSize ? currBuffSize - scrollMetrics.accu.currBufferIdx : scrollPadding;

            ScreenController.getScreen().scrollLines(MARGIN_TOP, metrics.maxY, padding);

            metrics.minY -= padding;
            metrics.currY -= padding;

            drawDownEntryRange(metrics, scrollMetrics.accu, (scrollMetrics.accu.currBufferIdx + padding), padding);

            scrollCursor(metrics);

            ScreenController.refresh();

            updateScrollDownMetrics(bus, scrollMetrics, padding);

            if (!isBottomEndScroll(bus, scrollMetrics)) {
                updateScrollDownMetrics(bus, scrollMetrics.accu, padding);
                if (padding < scrollPadding) {
                    scrollDown(bus, metrics, scrollMetrics, scrollPadding - padding);
                }
            }
        }
    }

    private static void updateScrollDownMetrics(final IOHandler bus, final ScrollMetrics scrollMetrics, int scrollPadding) {
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
                scrollMetrics.currBufferIdx = 0;
                updateScrollDownMetrics(bus, scrollMetrics, rest);
            }
        }
    }

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
    }

    private static void updateScrollUpMetrics(final IOHandler bus, final ScreenMetrics metrics, final ScrollMetrics scrollMetrics, final int newBuffIdx) {
        scrollMetrics.currBufferIdx = newBuffIdx;

        if (scrollMetrics.currBufferIdx < 0 && scrollMetrics.inputEntryIdx > 0) {
            final boolean noDecrement = !scrollMetrics.isInput && scrollMetrics.inputEntryIdx == scrollMetrics.outputEntryIdx;
            scrollMetrics.isInput = !scrollMetrics.isInput;

            if (scrollMetrics.getEntryIdx() - 1 >= 0) {
                if (!noDecrement)
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
}
