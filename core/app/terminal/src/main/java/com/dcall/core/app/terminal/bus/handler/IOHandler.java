package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.bus.parser.solver.BuiltInOperandSolver;
import com.dcall.core.app.terminal.bus.parser.solver.BuiltInOperatorSolver;
import com.dcall.core.configuration.app.service.builtin.BuiltInService;
import com.dcall.core.configuration.app.service.builtin.BuiltInServiceImpl;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.vertx.constant.URIConfig;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.service.message.MessageServiceImpl;
import com.dcall.core.configuration.generic.parser.Parser;
import com.dcall.core.configuration.utils.HelpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class IOHandler {
    private static final Logger LOG = LoggerFactory.getLogger(IOHandler.class);
    private RuntimeContext runtimeContext;
    private final MessageService messageService = new MessageServiceImpl();
    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();
    private final BuiltInService builtInService = new BuiltInServiceImpl();
    private String lastInput = null;

    public void init(final RuntimeContext context) {
        this.runtimeContext = context;
        this.builtInService.setContext(this.runtimeContext).setHelp(HelpUtils.getHelpPath(HelpUtils.HELP));

        builtInService.setParser(new Parser(new BuiltInOperatorSolver(runtimeContext), new BuiltInOperandSolver()));
    }

    public boolean handleInput() {
        lastInput = inputHandler.currentToString();

        output().addEntry();

        if (!lastInput.isEmpty() && !close()) {
            lockDisplay();
            final byte[] builtInResult = builtInService.run(new String[] { lastInput.trim().toLowerCase() });

            if (builtInResult == null)
                sendLastInput();
            else {
                output().addToEntry(new String(builtInResult));
                unlockDisplay();
            }

            return true;
        }
        return false;
    }

    public boolean reset() {
        lastInput = inputHandler.currentToString();

        if (lastInput.toLowerCase().equals("reset")) {
            inputHandler.reset();
            outputHandler.reset();

            if (DisplayController.isLocked())
                unlockDisplay();

            return true;
        }

        return false;
    }

    public void sendLastInput() {
        messageService.sendInputMessage(URIConfig.CMD_LOCAL_PROCESSOR_CONSUMER, lastInput.getBytes(),
                null,
                failed -> {
                    if (!failed.cause().getMessage().isEmpty())
                        output().addToEntry(failed.cause().getMessage());
                }, () -> unlockDisplay());
    }

    private void lockDisplay() {
        DisplayController.lock();
        DisplayController.setDataReady(false);
    }

    private void unlockDisplay() {
        lastInput = null;
        DisplayController.setDataReady(true);
    }

    private boolean close() {
        final boolean close = lastInput.trim().toLowerCase().equals("exit");

        if (close)
            ScreenController.stop();

        return close;
    }

    // GETTERS
    public final InputHandler input() { return this.inputHandler; }
    public final OutputHandler output() { return this.outputHandler; }
    public final List<InputEntry<String>> entries(final boolean isInput) { return isInput ? input().entries() : output().entries(); }

    // UTILS
    private String[] lastInputToArray() { return lastInput.trim().toLowerCase().split(" "); }
}
