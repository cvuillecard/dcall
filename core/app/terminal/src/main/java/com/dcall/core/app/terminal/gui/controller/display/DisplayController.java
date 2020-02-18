package com.dcall.core.app.terminal.gui.controller.display;

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

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.screenPosX;
import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.screenPosY;

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
            metrics.currX = TermAttributes.getTotalLineWidth();
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

    private static void drawInputEntry(InputEntry<String> entry) {
        final InputLine<String> currLine = entry.getBuffer().get(entry.posY());

        TextDrawer.drawString(screenPosX(entry.posX()), screenPosY(entry.posY()),
                currLine.toString().substring(entry.posX(), currLine.size()));

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

    public static int moveBeforeX(final ScreenMetrics metrics) {
        try {
            if (metrics.currX - 1 == ScreenController.getTerminal().getTerminalSize().getColumns()) {
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
