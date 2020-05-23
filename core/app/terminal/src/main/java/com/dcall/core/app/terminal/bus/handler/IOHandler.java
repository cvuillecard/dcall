package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.bus.input.InputEntry;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.vertx.constant.URIConfig;
import com.dcall.core.configuration.app.user.credential.CredentialInfo;
import com.dcall.core.configuration.app.service.message.MessageService;
import com.dcall.core.configuration.app.service.message.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class IOHandler {
    private static final Logger LOG = LoggerFactory.getLogger(IOHandler.class);
    private final MessageService messageService = new MessageServiceImpl();
    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();
    private String lastInput = null;
    private CredentialInfo credential = new CredentialInfo();

    public void initCredentials() {
        if (!credential.hasUser()) {
            getCredential();
        }
    }

    private boolean getCredential() {
//        final byte[] datas = CredentialAction.HAS_ROOT.getBytes();
//        sendInputMessage(datas, null);

        return false;
    }

    public boolean handleInput() {
        lastInput = inputHandler.currentToString();

        output().addEntry();

        if (!lastInput.isEmpty() && !close()) {
            sendLastInput();
            return true;
        }
        return false;
    }

    public boolean reset() {
        lastInput = inputHandler.currentToString();

        if (lastInput.toLowerCase().equals("reset")) {
            inputHandler.reset();
            outputHandler.reset();

            return true;
        }

        return false;
    }

    public void sendLastInput() {
        lockDisplay();
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

    public final CredentialInfo credentials() { return credential; }

    public final boolean hasUser() {
        return credential.hasUser();
    }
}
