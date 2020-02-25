package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import static com.dcall.core.app.terminal.gui.configuration.TermAttributes.PROMPT;

public final class IOHandler {
    private static final Logger LOG = LoggerFactory.getLogger(IOHandler.class);
    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();
    private final ProcessBuilder processBuilder = new ProcessBuilder();
    private final File pipe = new File("pipe");
    private Process process = null;
    private int _BUFFER_SIZE = 2048;

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
            process.waitFor();
            readOutput();
        } catch (IOException | InterruptedException e) {
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
                LOG.debug(new String(buffer).trim());
            }

            cin.close();
        }
        return this;
    }

    public void handleInput() {
        final String cmd = inputHandler.current().toString().substring(PROMPT.length());
        this.execute(cmd.split(" "));
    }

    public void prevEntry() {

    }

    // GETTERS
    public final InputHandler input() { return inputHandler; }
    public final OutputHandler output() { return outputHandler; }
}
