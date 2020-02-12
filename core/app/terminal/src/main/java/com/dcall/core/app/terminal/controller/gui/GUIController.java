package com.dcall.core.app.terminal.controller.gui;

import com.dcall.core.app.terminal.configuration.TermAttributes;
import com.dcall.core.app.terminal.controller.gui.handler.IOHandler;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GUIController { // IOHandler -> InputHandler::InputEntries[INPUT_PAGE_SIZE] / OutputHandler::OutputEntries[OUTPUT_PAGE_SIZE] -> KeyBoardController
    private static final Logger LOG = LoggerFactory.getLogger(GUIController.class);
    private static Terminal terminal;
    private static Screen screen;

    public static final void init() {
        ScreenController.init();
        KeyboardController.init();
        DisplayController.init(ScreenController.metrics());

        terminal = ScreenController.getTerminal();
        screen = ScreenController.getScreen();
    }

    public static void prompt(final boolean firstTime, final IOHandler ioHandler, final ScreenMetrics metrics) {
        if (firstTime)
            metrics.currY += 1;

        if (metrics.currY != metrics.maxHeight)
            metrics.currY++;
//        else
//            scrollUp(TermAttributes.SCROLL_PADDING_UP);
        DisplayController.prompt(firstTime, metrics);

        ioHandler.input().addEntry(TermAttributes.PROMPT);

        ScreenController.refresh();
    }

//    private static void handle() throws IOException {
//        prompt(true);
//        while (isUp()) {
//            TerminalUI.screen.doResizeIfNecessary();
//            readInput();
//            if (TerminalUI.keyPressed != null) {
//                switch (TerminalUI.keyPressed.getKeyType()) {
//                    case Character:
//                        handleCharacter();
//                        break;
//                    case Enter:
//                        prompt(false);
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
