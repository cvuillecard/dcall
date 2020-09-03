package com.dcall.core.configuration.app.verticle.task;

import com.dcall.core.configuration.generic.cluster.vertx.AbstractContextVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class TaskExecutorVerticle extends AbstractContextVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(TaskExecutorVerticle.class);
    private final long DELAY_MS = 300l;

    @Override
    public void start() throws Exception {
        vertx.setPeriodic(DELAY_MS, h -> {
//            LOG.info(this.getClass().getSimpleName() + " : Task execution handling");
        });
    }
}
