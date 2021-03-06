package com.dcall.core.app.terminal.vertx;

import com.dcall.core.app.terminal.gui.GUIProcessor;
import com.dcall.core.configuration.app.constant.TaskStatus;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.entity.task.Task;
import com.dcall.core.configuration.generic.cluster.vertx.AbstractTaskVerticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public final class TaskExecutorVerticle extends AbstractTaskVerticle {
    private int idx = 1;

    @Autowired
    protected TaskExecutorVerticle(final RuntimeContext runtimeContext) {
        super(runtimeContext);
    }

    @Override
    protected void onUpdate(final Task subTask) {
        GUIProcessor.bus().output().addToEntry(idx++ + " - " + subTask.toString());
    }

    @Override
    protected void onRemove() {
        GUIProcessor.bus().unlockDisplay();
    }

    @Override
    protected void onComplete() {
        GUIProcessor.bus().output().addToEntry(idx + " > TASK COMPLETED");
        reset();
    }

    @Override
    protected void onFail() {
        GUIProcessor.bus().output().addToEntry(idx + " > TASK FAILURE [ " + taskContext.getCurrent().getTask().getId() + " - " + TaskStatus.FAILED.name() + " ]");
        reset();
    }

    private void reset() {
        idx = 0;
    }
}
