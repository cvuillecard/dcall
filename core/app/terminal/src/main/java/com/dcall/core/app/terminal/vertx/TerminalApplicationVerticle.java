package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.app.terminal.gui.configuration.ScreenAttributes;
import com.dcall.core.configuration.generic.cluster.vertx.AbstractContextVerticle;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class TerminalApplicationVerticle extends AbstractContextVerticle {

    @Override
    protected void setUriContext() {
        uriContext.setBaseRemoteAppUri(uriContext.getLocalUri("processor.vertx.command"));

        uriContext.setLocalConsumerUri(InputConsumerVerticle.class.getName());
        uriContext.setRemoteConsumerUri(uriContext.getRemoteUri("CommandProcessorConsumerVerticle"));
    }

    @Override
    public void start() {
        runtimeContext.systemContext().routeContext().getVertxContext().setVertxURIContext(uriContext);
        GUIProcessor.start(runtimeContext, ScreenAttributes.FrameType.AWT_FRAME);
    }
}
