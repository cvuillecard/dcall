package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import io.vertx.core.AbstractVerticle;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class TerminalApplicationVerticle extends AbstractVerticle {
    @Override
    public void start() { GUIProcessor.start(); }
}
