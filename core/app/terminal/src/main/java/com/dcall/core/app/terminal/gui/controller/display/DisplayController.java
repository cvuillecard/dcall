package com.dcall.core.app.terminal.gui.controller.display;

import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.bus.output.InputLine;
import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.cursor.CursorController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.service.drawer.TextDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.*;

public final class DisplayController {
    private static final Logger LOG = LoggerFactory.getLogger(DisplayController.class);

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

        TextDrawer.drawString(metrics.currX++, metrics.currY, character);

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
        TextDrawer.drawString(metrics.screenPosX(entry.posX()), metrics.screenPosY(entry.posY()),
                entry.current().toString().substring(entry.posX(), entry.current().size()));

        if (entry.posY() < entry.maxNbLine()) {
            IntStream.range(entry.posY() + 1, entry.nbLine()).forEach(y ->
                    TextDrawer.drawString(metrics.screenPosX(0), metrics.screenPosY(y), entry.getBuffer().get(y).toString())
            );
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

        IntStream.range(nextY, entry.nbLine()).forEach(y -> TextDrawer.drawString(metrics.screenPosX(0), metrics.screenPosY(y), entry.getBuffer().get(y).toString()));
    }

    public static void drawCurrentInputLine(final InputEntry<String> entry, final ScreenMetrics metrics) {
        if (metrics.posY() == 0 && metrics.posX() < PROMPT.length()) {
            TextDrawer.drawPrompt(metrics);
            metrics.currX = metrics.screenPosX(PROMPT.length());
        }

        TextDrawer.drawString(metrics.currX, metrics.currY, entry.getBuffer().get(metrics.posY()).toString().substring(metrics.posX()));
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

    public static void clearScreen(final InputEntry<String> entry, final ScreenMetrics metrics) {
        if (metrics.minY > MARGIN_TOP) {
            final int distance = metrics.minY - MARGIN_TOP;
            ScreenController.getScreen().scrollLines(MARGIN_TOP, metrics.maxY, distance);
            ScreenController.refresh();
            entry.setX(metrics.posX());
            entry.setY(0);
            metrics.minY = MARGIN_TOP;
            metrics.currY = metrics.screenPosY(entry.posY());

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
                entry.setX(PROMPT.length());
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
}
