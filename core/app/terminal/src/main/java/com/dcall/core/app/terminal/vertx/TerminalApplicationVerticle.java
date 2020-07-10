package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.app.terminal.gui.configuration.ScreenAttributes;
import com.dcall.core.configuration.app.context.RuntimeContext;
import io.vertx.core.AbstractVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class TerminalApplicationVerticle extends AbstractVerticle {
    @Autowired private RuntimeContext runtimeContext;

    @Override
    public void start() {
        GUIProcessor.start(runtimeContext, ScreenAttributes.FrameType.AWT_FRAME);
    }
}
