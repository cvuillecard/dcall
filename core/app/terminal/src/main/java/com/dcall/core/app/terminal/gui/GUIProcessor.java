package com.dcall.core.app.terminal.gui;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.cursor.CursorController;
import com.dcall.core.app.terminal.gui.controller.keyboard.KeyboardController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.bus.handler.IOHandler;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenMetrics;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class GUIProcessor { // IOHandler -> InputHandler::InputEntries[INPUT_PAGE_SIZE] / OutputHandler::OutputEntries[OUTPUT_PAGE_SIZE] -> KeyBoardController
    private static final Logger LOG = LoggerFactory.getLogger(GUIProcessor.class);
    private static final IOHandler bus = new IOHandler();
    private static Terminal terminal;
    private static Screen screen;

    public static void start() {
        GUIProcessor.init();
        GUIProcessor.loop();
    }

    public static final void init() {
        ScreenController.init();

        terminal = ScreenController.getTerminal();
        screen = ScreenController.getScreen();

        KeyboardController.init(terminal, bus);
        CursorController.init(screen);
        DisplayController.init(ScreenController.metrics());
    }

    public static void prompt(final boolean firstTime, final ScreenMetrics metrics) {
        if (firstTime)
            metrics.currY += 1;

        if (metrics.currY != metrics.maxHeight)
            metrics.currY++;
        else
            DisplayController.scrollUp(metrics, TermAttributes.SCROLL_PADDING_UP);
        
        bus.input().addEntry(TermAttributes.PROMPT + ' ');

        DisplayController.displayPrompt(metrics);

    }

    public static final void flush() {
        try {
            terminal.flush();
        }
        catch (IOException e) {
            LOG.error(GUIProcessor.class.getName() + " > ERROR < " + e.getMessage());
        }
    }

    public static final void loop() {

        GUIProcessor.prompt(true, ScreenController.metrics());

        while (ScreenController.isUp()) {
//            screen.doResizeIfNecessary();
            KeyboardController.handleKeyboard();
        }

        GUIProcessor.close();
    }

    private static final void close() {
        ScreenController.close();
    }

//    private static void loop() throws IOException {
//        drawPrompt(true);
//        while (isUp()) {
//            TerminalUI.screen.doResizeIfNecessary();
//            readInput();
//            if (TerminalUI.keyPressed != null) {
//                switch (TerminalUI.keyPressed.getKeyType()) {
//                    case Character:
//                        handleCharacter();
//                        break;
//                    case Enter:
//                        drawPrompt(false);
//                        break;
//                    case Backspace:
//                        deleteCharacter();
//                        break;
//                    case ArrowUp:
//                        scrollDown(TermAttributes.SCROLL_PADDING_DOWN);
//                        screen.refresh();
//                        break;
//                    default:
//                        break;
//                }
//                TerminalUI.term.flush();
//            }
//        }
//        TerminalUI.close();
//    }
}
