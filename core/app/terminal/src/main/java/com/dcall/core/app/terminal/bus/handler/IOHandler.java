package com.dcall.core.app.terminal.bus.handler;

import com.dcall.core.app.terminal.gui.configuration.TermAttributes;
import com.dcall.core.app.terminal.gui.controller.display.DisplayController;
import com.dcall.core.app.terminal.gui.controller.screen.ScreenController;
import com.dcall.core.app.terminal.vertx.constant.URIConfig;
import com.dcall.core.configuration.entity.MessageBean;
import com.dcall.core.configuration.utils.StringUtils;
import com.dcall.core.configuration.vertx.cluster.HazelcastCluster;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class IOHandler {
    private static final Logger LOG = LoggerFactory.getLogger(IOHandler.class);
    private final InputHandler inputHandler = new InputHandler();
    private final OutputHandler outputHandler = new OutputHandler();
    private String lastInput = null;

    public boolean handleInput() {
        lastInput = StringUtils.epur(inputHandler.current().toString().substring(TermAttributes.getPrompt().length()));

        if (!lastInput.isEmpty() && !close()) {
            sendLastInput();
            return true;
        }
        return false;
    }

    public void sendLastInput() {
        DisplayController.lock();
        DisplayController.setDataReady(false);
        output().addEntry();
        final byte[] datas = lastInput.getBytes();
        final com.dcall.core.configuration.bo.Message<String> msg = new MessageBean(HazelcastCluster.getLocalUuid(), datas, datas.length);
        Vertx.currentContext().owner().eventBus()
                .send(URIConfig.CMD_LOCAL_PROCESSOR_CONSUMER, Json.encodeToBuffer(msg), res -> {
                    if (res.succeeded()) {
                        LOG.debug(" > GUI command traited by remote processor : replied > " + res.result());
                    } else {
                        final String msgError = IOHandler.class.getName() + res.cause().getMessage();
                        if (!res.cause().getMessage().isEmpty())
                            output().addToEntry(res.cause().getMessage());
                        LOG.error(msgError);
                    }
                    lastInput = null;
                    DisplayController.setDataReady(true);
                });

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
}
