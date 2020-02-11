package com.dcall.core.app.terminal.gui;

import com.dcall.core.app.terminal.configuration.TermAttributes;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.SimpleTerminalResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import javax.swing.*;


public final class TerminalUI {
    private static final Logger LOG = LoggerFactory.getLogger(TerminalUI.class);

    private static boolean up = true;
    private static Terminal term;
    private static KeyStroke keyPressed;
    private static Screen screen;
    private static Window window;
    private static int width;
    private static int height;
    private static int limitWidth;
    private static int limitHeight;
    private static int currX = 0;
    private static int currY = 0;
    private static final StringBuffer buffer = new StringBuffer();
    private static final Map<String, Runnable> controlKeys = new HashMap<>();

    private static final void init() throws IOException {
        TerminalUI.initScreen();
        TerminalUI.initControlKeys();
        TerminalUI.initDisplay();
    }

    private static final void resetPosition() {
        currX = 0;
        currY = 2;
    }

    private static final void refresh() {
        try {
            TerminalUI.screen.doResizeIfNecessary();
            TerminalUI.screen.refresh();
        } catch (IOException e) {
            LOG.error(TerminalUI.class.getName() + " - ERROR > " + e.getMessage());
        }
    }

    private static final void clearScreen() {
        final int margin = 2;
        final int divisor = width;
        final int inputLength = buffer.length() + TermAttributes.HEADER_TITLE.length();
        final int nbInputLines = inputLength / divisor;
        final int rest = inputLength % divisor;
        final int totalInputLines = nbInputLines + (rest > 0 ? 1 : 0);
        final int posY = margin + (totalInputLines > 1 ? nbInputLines : 0);

        TerminalUI.screen.scrollLines(margin, currY, totalInputLines > 1 ? (currY - nbInputLines - margin) : (currY - margin));
        TerminalUI.screen.setCursorPosition(new TerminalPosition(TerminalUI.currX, posY));
        TerminalUI.screen.newTextGraphics().drawLine(0, 1, width, 1, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.PROMPT_BACKGROUND));
        currY = posY;

        TerminalUI.refresh();
    }

    private static void initControlKeys() {
        controlKeys.put("l", TerminalUI::clearScreen);
        controlKeys.put("L", TerminalUI::clearScreen);
    }

    private static final void initDisplay() throws IOException {
        initHeader();
        currX++;
        screen.refresh();
    }

//    private static final void initWindow() {
//        TerminalUI.window = new BasicWindow();
//        initWindowListeners();
//        WindowBasedTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLACK));
//        gui.addWindowAndWait(window);
//    }

//    private static final void initWindowListeners() {
//        TerminalUI.window.addWindowListener(new WindowListener() {
//            @Override
//            public void onResized(Window window, TerminalSize terminalSize, TerminalSize terminalSize1) {
//            }
//
//            @Override
//            public void onMoved(Window window, TerminalPosition terminalPosition, TerminalPosition terminalPosition1) {
//            }
//
//            @Override
//            public void onInput(Window window, KeyStroke keyStroke, AtomicBoolean atomicBoolean) {
////                if (keyStroke != null) {
////                    LOG.info("key pressed : " + keyStroke.toString());
////                }
//            }
//
//            @Override
//            public void onUnhandledInput(Window window, KeyStroke keyStroke, AtomicBoolean atomicBoolean) {
//            }
//        });
//    }

    private static void initHeader() {
        TerminalUI.screen.newTextGraphics().drawLine(0, 0, width, 0, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.HEADER_BACKGROUND));
        TerminalUI.screen.newTextGraphics()
                .setBackgroundColor(TermAttributes.HEADER_BACKGROUND)
                .setForegroundColor(TermAttributes.HEADER_FOREGROUND)
                .putString((width / 2) - (TermAttributes.HEADER_TITLE.length() / 2), 0,
                        TermAttributes.HEADER_TITLE, SGR.BOLD);
    }

    private static final void initScreen() {
        try {
            TerminalUI.term = new DefaultTerminalFactory()
                    .setTerminalEmulatorTitle(TermAttributes.FRAME_TITLE)
                    .setInitialTerminalSize(new TerminalSize(TermAttributes.FRAME_NB_COLS, TermAttributes.FRAME_NB_ROWS))
                    .createTerminal();

            addWindowListeners();

            TerminalUI.screen = new TerminalScreen(TerminalUI.term);

            setScreenSize(TermAttributes.FRAME_NB_COLS, TermAttributes.FRAME_NB_ROWS);

            TerminalUI.screen.startScreen();
            TerminalUI.screen.setCursorPosition(null);

            LOG.debug(TerminalUI.class.getName() + " > initScreen() > done.");
            LOG.debug(TerminalUI.class.getName() + "    | width = " + width + " columns");
            LOG.debug(TerminalUI.class.getName() + "    | height = " + height + " rows");
        } catch (final IOException e) {
            LOG.error(e.getMessage());
        }
    }

    private static void setScreenSize(final int nbCols, final int nbRows) {
        TerminalUI.width = nbCols;
        TerminalUI.height = nbRows;
        TerminalUI.limitWidth = TerminalUI.width - 1;
        TerminalUI.limitHeight = TerminalUI.height - 1;
    }

    private static final void addWindowListeners() {
        if (TerminalUI.term instanceof SwingTerminalFrame) {
            ((SwingTerminalFrame) term).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            addWindowListener();
            addResizeListener();
        }
    }

    private static void addResizeListener() {
        term.addResizeListener(new SimpleTerminalResizeListener(new TerminalSize(TermAttributes.FRAME_NB_COLS, TermAttributes.FRAME_NB_ROWS)) {
            @Override
            public synchronized void onResized(final Terminal terminal, final TerminalSize newSize) {
                super.onResized(terminal, newSize);
                try {
                    LOG.debug(" *** Window resized to : "
                            + terminal.getTerminalSize().getColumns() + " x "
                            + terminal.getTerminalSize().getRows());
                    setScreenSize(terminal.getTerminalSize().getColumns(), terminal.getTerminalSize().getRows());
                    ((SwingTerminalFrame)TerminalUI.term).setTitle(TermAttributes.FRAME_TITLE + " (" + width + 'x' + height + ')');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void addWindowListener() {
        ((SwingTerminalFrame) term).addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                TerminalUI.up = false;
            }
        });
    }

    private static final void readInput() throws IOException {
        TerminalUI.keyPressed = TerminalUI.term.pollInput();
    }

    public static final void start() {
        try {
            TerminalUI.init();
            TerminalUI.handle();
        } catch (IOException e) {
            LOG.error(TerminalUI.class.getName() + " - start() ERROR > " + e.getMessage());
        }
    }

    private static final void close() {
        try {
            TerminalUI.screen.stopScreen();
            TerminalUI.screen.close();
        } catch (IOException e) {
            LOG.error(TerminalUI.class.getName() + " - close() ERROR > " + e.getMessage());
        }
    }

    private static void handle() throws IOException {
        prompt(true);
        while (TerminalUI.up) {
            TerminalUI.screen.doResizeIfNecessary();
            readInput();
            if (TerminalUI.keyPressed != null) {
                switch (TerminalUI.keyPressed.getKeyType()) {
                    case Character:
                        handleCharacter();
                        break;
                    case Enter:
                        prompt(false);
                        break;
                    case Backspace:
                        deleteCharacter();
                        break;
                    case ArrowUp:
                        scrollDown(TermAttributes.SCROLL_PADDING_DOWN);
                        screen.refresh();
                        break;
                    default:
                        break;
                }
                TerminalUI.term.flush();
            }
        }
        TerminalUI.close();
    }

    private static final void deleteCharacter() throws IOException {
        if ((currX - 1) > TermAttributes.PROMPT.length()) {
            final int posX = TerminalUI.currX - 1;

            TerminalUI.screen.newTextGraphics().setCharacter(new TerminalPosition(posX, TerminalUI.currY), ' ');
            TerminalUI.screen.setCursorPosition(new TerminalPosition(posX, TerminalUI.currY));

            currX = posX;
            buffer.setLength(buffer.length() - 1);

            screen.refresh();
        }
    }

    private static final boolean handleSpecialChar() {
        if (keyPressed.isCtrlDown()
                && controlKeys.get(keyPressed.getCharacter().toString()) != null) {
            controlKeys.get(keyPressed.getCharacter().toString()).run();
            return true;
        }
        return false;
    }

    private static final int moveX() throws IOException {
        if (currX + 1 == term.getTerminalSize().getColumns()) {
            currY += 1;
            currX = 1;

            return currX;
        }

        return currX++;
    }

    private static final void moveCursor() throws IOException {
        LOG.info(" cursor X = " + TerminalUI.currX);
        LOG.info(" cursor Y = " + TerminalUI.currY);
        if (TerminalUI.currX == TerminalUI.limitWidth && TerminalUI.currY == TerminalUI.limitHeight)
            scrollUp(TermAttributes.SCROLL_PADDING_UP);

        if (TerminalUI.currX == TerminalUI.limitWidth) {
            TerminalUI.currX = 1;
            TerminalUI.currY += (TerminalUI.currY == TerminalUI.limitHeight ? 0 : 1);
        }

        TerminalUI.screen.setCursorPosition(new TerminalPosition(TerminalUI.currX + 1, TerminalUI.currY));
        TerminalUI.screen.refresh();
    }

    private static void scrollUp(final int distance) {
        TerminalUI.screen.scrollLines(1, TerminalUI.height, distance);
    }

    private static void scrollDown(final int distance) {
        TerminalUI.screen.scrollLines(1, TerminalUI.height, (distance > 0 ? distance * -1 : distance));
    }

    private static final void displayCharacter() throws IOException {
        LOG.debug("character : " + keyPressed.getCharacter());
        moveCursor();
        TerminalUI.screen.newTextGraphics()
                .setBackgroundColor(TermAttributes.INPUT_BACKGROUND)
                .setForegroundColor(TermAttributes.INPUT_FOREGROUND)
                .putString(moveX(), currY, keyPressed.getCharacter().toString(), SGR.BOLD);

        screen.refresh();
    }

    private static final void handleCharacter() throws IOException {
        if (handleClose()) {
            buffer.append(keyPressed.getCharacter());
            if (!handleSpecialChar())
                displayCharacter();
        }
    }

    private static void prompt(final boolean firstTime) throws IOException {
        buffer.setLength(0);
        if (firstTime)
            currY += 1;
        if (TerminalUI.currY != TerminalUI.limitHeight)
            currY++;
        else
            scrollUp(TermAttributes.SCROLL_PADDING_UP);
        TerminalUI.screen.newTextGraphics().drawLine(0, currY, TermAttributes.PROMPT.length(), currY, new TextCharacter(' ')
                .withBackgroundColor(TermAttributes.PROMPT_BACKGROUND));
        TerminalUI.screen.newTextGraphics()
                .setBackgroundColor(TermAttributes.PROMPT_BACKGROUND)
                .setForegroundColor(TermAttributes.PROMPT_FOREGROUND)
                .putString(0, currY, TermAttributes.PROMPT, SGR.BOLD);

        currX = TermAttributes.PROMPT.length() + 1;
        TerminalUI.screen.setCursorPosition(new TerminalPosition(currX, currY));

        screen.refresh();
    }

    private static final boolean handleClose() {
        if ((keyPressed.isCtrlDown() &&
                (keyPressed.getCharacter().charValue() == 'c' || (keyPressed.getCharacter().charValue() == 'C')))
                || keyPressed.getKeyType().equals(KeyType.Escape))
            TerminalUI.up = false;

        return TerminalUI.up;
    }
}
