package com.dcall.core.app.terminal.gui.controller.display;

import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.bus.output.InputLine;
import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.cursor.CursorController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.service.drawer.TextDrawer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.IntStream;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.*;

public final class DisplayController {
    private static final Logger LOG = LoggerFactory.getLogger(DisplayController.class);

    public static void init(final ScreenMetrics metrics) {
        TextDrawer.drawHeader(metrics.width);
        metrics.currX++;
    }

    public static void displayPrompt(final ScreenMetrics metrics) {
        TextDrawer.drawPrompt(metrics);

        metrics.currX = TermAttributes.getPromptStartIdx();

        CursorController.moveAt(metrics);

        ScreenController.refresh();
    }

    public static void displayCharacter(final InputEntry<String> entry, final ScreenMetrics metrics, final String character) {
        LOG.debug("character : " + character);

        TextDrawer.drawString(DisplayController.moveAfterX(metrics), metrics.currY, character);

        if (!entry.isAppend())
            drawInputEntry(entry);

        CursorController.moveAfter(metrics);

        ScreenController.refresh();
    }

    public static void deleteCharacter(final InputEntry<String> entry, final ScreenMetrics metrics) {

        if (metrics.currX == metrics.minWidth) {
            metrics.currX = getTotalLineWidth();
            metrics.currY--;
        }
        else
            metrics.currX--;

        TextDrawer.drawCharacter(metrics.currX, metrics.currY, ' ');

        if (!entry.isAppend()) {
            drawInputEntry(entry);
            final InputLine<String> lastLine = entry.getBuffer().get((entry.maxNbLine()));
            TextDrawer.drawCharacter(screenPosX(lastLine.getBuffer().size()), screenPosY(entry.maxNbLine()), ' ');
        }

        CursorController.moveAt(metrics);

        ScreenController.refresh();
    }

    private static void drawInputEntry(final InputEntry<String> entry) {
        TextDrawer.drawString(screenPosX(entry.posX()), screenPosY(entry.posY()),
                entry.current().toString().substring(entry.posX(), entry.current().size()));

        if (entry.posY() < entry.maxNbLine()) {
            IntStream.range(entry.posY() + 1, entry.nbLine()).forEach(y ->
                    TextDrawer.drawString(screenPosX(0), screenPosY(y), entry.getBuffer().get(y).toString())
            );
        }
    }

    public static void moveAt(final ScreenMetrics metrics) {
        CursorController.moveAt(metrics);
        ScreenController.refresh();
    }

    private static void drawBlankFromPos(final InputEntry<String> entry) {
        final int nextY = entry.posY() + 1;
        // Lanterna : TextGraphics.drawLine() doesn't handle linear drawing from a start position to an end position with a different Y value -> anyway seems to be bugged or not complete
        // Only startY seems to be considered by drawLine, so we can only draw the current rest of line from current position
        TextDrawer.drawBlank(screenPosX(entry.posX()), screenPosY(entry.posY()), screenPosX(entry.current().size()), screenPosY(entry.posY()));
        // because of drawLing incapacity and drawRectangle bugging if more than 3 lines to draw, i have not the choice of only print line by line...sorry, or i have to code a graphical library using swing
        IntStream.range(nextY, entry.nbLine()).forEach(y -> TextDrawer.drawBlank(screenPosX(0), screenPosY(y), screenPosX(entry.getBuffer().get(y).size() - 1), screenPosY(y)));
    }

    public static void cut(final IOHandler bus) {
        final InputEntry<String> entry = bus.input().current();
        final InputLine<String> input = new InputLine<>();

        drawBlankFromPos(entry);

        bus.input().entryToInputLineFromPos(entry, input);
        bus.input().cleanEntryFromPos(entry);
        bus.input().clipBoard().setContent(input.toString());

        ScreenController.refresh();
    }

    private static void drawLineFromPos(final InputEntry<String> entry, final ScreenMetrics metrics) {
        int nextY = entryPosY(metrics.currY) + 1;

        TextDrawer.drawString(metrics.currX, metrics.currY, entry.getBuffer().get(entryPosY(metrics.currY)).toString().substring(entryPosX(metrics.currX)));

        IntStream.range(nextY, entry.nbLine()).forEach(y -> TextDrawer.drawString(screenPosX(0), screenPosY(y), entry.getBuffer().get(y).toString()));
    }

    public static void paste(final InputEntry<String> entry, final int length, final ScreenMetrics metrics) {
        drawBlankFromPos(entry);

        drawLineFromPos(entry, metrics);

        entry.setX(entryPosX(metrics.currX));
        entry.setY(entryPosY(metrics.currY));

        entry.moveX(length);

        metrics.currX = screenPosX(entry.posX());
        metrics.currY = screenPosY(entry.posY());

        moveAt(metrics);

        ScreenController.refresh();
    }

    public static int moveAfterX(final ScreenMetrics metrics) {
        try {
            if (metrics.currX + 1 == ScreenController.getTerminal().getTerminalSize().getColumns()) {
                metrics.currY += 1;
                metrics.currX = 1;

                return metrics.currX;
            }
        }
        catch (IOException e) {
            LOG.error(DisplayController.class.getName() + " > ERROR < " + e.getMessage());
        }

        return metrics.currX++;
    }

    public static void scrollUp(final ScreenMetrics metrics, final int distance) {
        ScreenController.getScreen().scrollLines(1, metrics.height, distance);
    }

    public static void scrollDown(final ScreenMetrics metrics, final int distance) {
        ScreenController.getScreen().scrollLines(1, metrics.height, (distance > 0 ? distance * -1 : distance));
    }
}
