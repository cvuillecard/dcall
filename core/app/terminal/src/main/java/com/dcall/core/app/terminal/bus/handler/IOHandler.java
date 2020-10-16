package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.configuration.app.context.vertx.uri.VertxURIContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.provider.message.MessageServiceProvider;
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
import com.dcall.core.configuration.utils.URIUtils;
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
        this.messageService = runtimeContext.serviceContext().serviceProvider().messageServiceProvider().messageService();

        this.builtInService.setRuntimeContext(this.runtimeContext).setHelp(HelpUtils.getHelpPath(HelpUtils.HELP));
        builtInService.setParser(new Parser(new BuiltInOperatorSolver(runtimeContext), new BuiltInOperandSolver()));
    }

    public boolean handleInput() {
        lastInput = inputHandler.currentToString();

        output().addEntry();

        final boolean exit = isExit();
        if (!lastInput.isEmpty() && !close(exit)) {
            lockDisplay();
            byte[] builtInResult = null;
            final boolean isLocalMode = runtimeContext.serviceContext().serviceProvider().environService().getInterpretMode(runtimeContext);

            if (isLocalMode && !exit)
                builtInResult = builtInService.run(new String[] { lastInput.trim().toLowerCase() });

            if (exit)
                output().addToEntry("Interpreter switched to local mode.");
            else if (builtInResult == null)
                sendLastInput();
            else
                output().addToEntry(new String(builtInResult));

            if (exit || builtInResult != null && !runtimeContext.clusterContext().taskContext().isCurrentTask())
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
        try {
            final VertxURIContext uriContext = this.runtimeContext.systemContext().routeContext().getVertxContext().getVertxURIContext();
            final MessageServiceProvider messageServiceProvider = this.runtimeContext.serviceContext().serviceProvider().messageServiceProvider();
            final FingerPrint<String> nextFingerPrint = messageServiceProvider.fingerPrintService().nextFingerPrint(this.runtimeContext.clusterContext().fingerPrintContext());
            final byte[] bytes = messageServiceProvider.messageService().encryptMessage(this.runtimeContext, nextFingerPrint, lastInput.toLowerCase().getBytes());

            messageService.send(URIUtils.getUri(uriContext.getRemoteConsumerUri(), nextFingerPrint.getId()), bytes,
                    null,
                    failed -> {
                        if (failed.cause().getMessage() != null)
                            output().addToEntry(failed.cause().getMessage());
                    }, () -> unlockDisplay());
        }
        catch (Exception e) {
            LOG.debug(e.getMessage());
            output().addToEntry(e.getMessage());
        }
    }

    private void lockDisplay() {
        DisplayController.lock();
        DisplayController.setDataReady(false);
    }

    public void unlockDisplay() {
        lastInput = null;
        DisplayController.setDataReady(true);
    }

    private boolean close(final boolean isExit) {
        if (isExit) {
            final boolean isLocalMode = runtimeContext.serviceContext().serviceProvider().environService().getInterpretMode(runtimeContext);

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
