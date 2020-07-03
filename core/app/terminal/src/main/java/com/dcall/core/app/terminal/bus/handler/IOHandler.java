package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import com.dcall.core.configuration.generic.parser.expression.operand.solver.impl.BuiltInOperandSolver;
import com.dcall.core.configuration.generic.parser.expression.operator.solver.impl.BuiltInOperatorSolver;
import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.InterpretMode;
import com.dcall.core.configuration.app.service.builtin.BuiltInService;
import com.dcall.core.configuration.app.service.builtin.BuiltInServiceImpl;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.generic.parser.Parser;
import com.dcall.core.configuration.utils.HelpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class IOHandler {
    private static final Logger LOG = LoggerFactory.getLogger(IOHandler.class);
    private RuntimeContext runtimeContext;
    private MessageService messageService;
    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();
    private final BuiltInService builtInService = new BuiltInServiceImpl();
    private String lastInput = null;

    public void init(final RuntimeContext context) {
        this.runtimeContext = context;
        this.messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().getMessageService();

        this.builtInService.setContext(this.runtimeContext).setHelp(HelpUtils.getHelpPath(HelpUtils.HELP));
        builtInService.setParser(new Parser(new BuiltInOperatorSolver(runtimeContext), new BuiltInOperandSolver()));
    }

    public boolean handleInput() {
        lastInput = inputHandler.currentToString();

        output().addEntry();

        final boolean exit = isExit();
        if (!lastInput.isEmpty() && !close(exit)) {
            lockDisplay();
            byte[] builtInResult = null;
            final boolean isLocalMode = Boolean.valueOf(runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.INTERPRET_MODE));

            if (isLocalMode && !exit)
                builtInResult = builtInService.run(new String[] { lastInput.trim().toLowerCase() });

            if (exit)
                output().addToEntry("Interpreter switched to local mode.");
            else if (builtInResult == null)
                sendLastInput();
            else
                output().addToEntry(new String(builtInResult));

            unlockDisplay();

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
        final VertxURIContext uriContext = this.runtimeContext.systemContext().routeContext().getVertxContext().getVertxURIContext();

        messageService.send(uriContext.getRemoteConsumerUri(), lastInput.toLowerCase().getBytes(),
                null,
                failed -> {
                    if (!failed.cause().getMessage().isEmpty())
                        output().addToEntry(failed.cause().getMessage());
                }, null);
    }

    private void lockDisplay() {
        DisplayController.lock();
        DisplayController.setDataReady(false);
    }

    private void unlockDisplay() {
        lastInput = null;
        DisplayController.setDataReady(true);
    }

    private boolean close(final boolean isExit) {
        if (isExit) {
            final boolean isLocalMode = Boolean.valueOf(runtimeContext.userContext().getEnviron().getProperties().getProperty(EnvironConstant.INTERPRET_MODE));

            if (isLocalMode)
                ScreenController.stop();
            else {
                runtimeContext.userContext().getEnviron().getProperties().setProperty(EnvironConstant.INTERPRET_MODE, String.valueOf(InterpretMode.LOCAL.mode()));
                return false;
            }
        }

        return isExit;
    }

    private boolean isExit() {
        return lastInput.trim().toLowerCase().equals("exit");
    }

    // GETTERS
    public final InputHandler input() { return this.inputHandler; }
    public final OutputHandler output() { return this.outputHandler; }
    public final List<InputEntry<String>> entries(final boolean isInput) { return isInput ? input().entries() : output().entries(); }
}
