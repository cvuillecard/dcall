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

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.getTotalLineWidth;
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

    public static void cut(final IOHandler bus, final ScreenMetrics metrics) {
        final InputEntry<String> entry = bus.input().current();
        final InputLine<String> input = new InputLine<>();
        int x = entry.posX();
        int y = entry.posY();
        int nextY = y + 1;

        // impossible de drawLine directement d'une position start a une position end avec un Y superieur et un X inferieur sans bug..j'ai du mal comprendre le fonctionnement ? ou vous n'avez pas utilise mon InputEntry ?..blague
        TextDrawer.drawBlank(screenPosX(entry.posX()), screenPosY(entry.posY()), screenPosX(entry.current().size()), screenPosY(entry.posY()));
        // donc .. drawRectangle
        if (y < entry.maxNbLine())
            TextDrawer.drawBlankRectangle(metrics.minWidth, screenPosY(nextY), getTotalLineWidth(), entry.nbLine() - nextY);

        while (y < entry.nbLine()) {
            while (x < entry.getBuffer().get(y).size()) {
                input.add(entry.getBuffer().get(y).getBuffer().get(x));
                entry.getBuffer().get(y).removeAt(x);
            }
            x = 0;
            y++;
        }

        while (entry.maxNbLine() > entry.posY())
            entry.getBuffer().remove(entry.maxNbLine());

        bus.input().clipBoard().setContent(input.toString());

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
