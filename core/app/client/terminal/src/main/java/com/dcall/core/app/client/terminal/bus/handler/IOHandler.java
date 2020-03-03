package com.dcall.core.app.client.terminal.bus.handler;

import com.dcall.core.app.client.terminal.bus.input.InputLine;
import com.dcall.core.app.client.terminal.gui.GUIProcessor;
import com.dcall.core.app.client.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.client.terminal.vertx.InputProducerVerticle;
import com.dcall.core.app.client.terminal.vertx.constant.URIConfig;
import com.dcall.core.configuration.utils.StringUtils;
import com.dcall.core.configuration.vertx.VertxApplication;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static com.dcall.core.app.client.terminal.gui.configuration.TermAttributes.PROMPT;

public final class IOHandler {
    private static final Logger LOG = LoggerFactory.getLogger(IOHandler.class);
    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();
    private final ProcessBuilder processBuilder = new ProcessBuilder();
    private final File pipe = new File("pipe");
    private Process process = null;
    private int _BUFFER_SIZE = 2048;
    private String lastInput = null;

    public void init() {
        initOutputFile();
        initRedirect();
    }

    private void initRedirect() {
        processBuilder.redirectOutput(pipe).redirectError(pipe);
    }

    private void initOutputFile() {
        if (pipe.exists())
            pipe.delete();
        try {
            pipe.createNewFile();
        } catch (IOException e) {
            LOG.error(this.getClass().getName() + " > ERROR < " + e.getMessage());
        }
    }

    public IOHandler execute(final String... cmd) {
        try {
            process = processBuilder.command(cmd).start();
//            process.waitFor();
            readOutput();
        } catch (IOException e) {
            output().addInputLine(lastInput + ": command not found");
            LOG.error(this.getClass().getName() + " > ERROR < " + e.getMessage());
        }
        return this;
    }

    public IOHandler readOutput() throws IOException {
        if (process != null) {
            final byte buffer[] = new byte[_BUFFER_SIZE];
            int nread;
            final InputStream cin = new DataInputStream(new FileInputStream(pipe));

            while ((nread = cin.read(buffer, 0, _BUFFER_SIZE)) > 0) {
                final String str = new String(buffer).trim();
                output().addInputLine(str);
                LOG.debug(str);
            }

            cin.close();
        }
        return this;
    }

    public void handleInput() {
        lastInput = StringUtils.epur(inputHandler.current().toString().substring(PROMPT.length()));

        if (!close()) {
            addOutput();
            sendLastInput();
            execute(lastInput.split(" "));
        }
    }

    public void sendLastInput() {
        if (lastInput != null) {
            Vertx.currentContext().owner().eventBus()
                    .send(URIConfig.CMD_PROCESSOR_CONSUMER, lastInput, res -> {
                        if (res.succeeded()) {
                            LOG.debug(" > GUI command traited by remote processor : replied > " + res.result());
                        } else {
                            LOG.error(IOHandler.class.getName() + " ERROR > " + res.cause().getMessage());
                        }
                        lastInput = null;
                    });
        }
    }

    private boolean close() {
        final boolean close = lastInput.trim().toLowerCase().equals("exit");

        if (close)
            ScreenController.stop();

        return close;
    }

    public final IOHandler addOutput() {
        output().entries().add(new InputLine<>());

        return this;
    }

    // GETTERS
    public final InputHandler input() { return this.inputHandler; }
    public final OutputHandler output() { return this.outputHandler; }
    public final String getLastInput() { return this.lastInput; }

    public final void setLastInput(final String lastInput) { this.lastInput = lastInput; }
}
