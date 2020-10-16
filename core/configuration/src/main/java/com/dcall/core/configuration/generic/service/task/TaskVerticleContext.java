package com.dcall.core.configuration.generic.service.task;

import com.dcall.core.configuration.app.context.WithRuntimeContext;
import com.dcall.core.configuration.app.context.task.AbstractTaskContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.Message;

public interface TaskVerticleContext extends WithRuntimeContext {
    // getters
    String getRunURI();
    String getCompleteURI();
    FingerPrint getFingerPrint();
    Message<String> getMsgTransporter();

    // setters
    TaskVerticleContext setRunURI(final String runURI);
    TaskVerticleContext setCompleteURI(final String completeURI);
    AbstractTaskContext setFingerPrint(final FingerPrint fingerPrint);
    AbstractTaskContext setMsgTransporter(Message<String> msgTransporter);
}
