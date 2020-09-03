package com.dcall.core.configuration.app.verticle.task;

import com.dcall.core.configuration.generic.cluster.vertx.AbstractContextVerticle;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class TaskConsumerVerticle extends AbstractContextVerticle {

    @Override
    public void start() throws Exception {}
}
