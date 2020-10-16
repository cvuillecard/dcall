package com.dcall.core.configuration.app.context.task;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.entity.fingerprint.FingerPrint;
import com.dcall.core.configuration.app.entity.message.Message;
import com.dcall.core.configuration.app.entity.message.MessageBean;
import com.dcall.core.configuration.generic.cluster.hazelcast.HazelcastCluster;
import com.dcall.core.configuration.generic.service.task.TaskVerticleContext;

public abstract class AbstractTaskContext implements TaskVerticleContext {
    protected final RuntimeContext runtimeContext;
    protected String runURI;
    protected String completeURI;
    protected FingerPrint<String> fingerPrint;
    protected Message<String> msgTransporter = new MessageBean().setId(HazelcastCluster.getLocalUuid());

    protected AbstractTaskContext(final RuntimeContext runtimeContext) { this.runtimeContext = runtimeContext; }

    public abstract AbstractTaskContext init();

    // getters
    @Override public RuntimeContext getRuntimeContext() { return runtimeContext; }
    @Override public String getRunURI() { return runURI; }
    @Override public String getCompleteURI() { return completeURI; }
    @Override public FingerPrint getFingerPrint() { return fingerPrint; }
    @Override public Message<String> getMsgTransporter() { return msgTransporter; }

    // setters
    @Override public AbstractTaskContext setRuntimeContext(final RuntimeContext runtimeContext) { return this; }
    @Override public AbstractTaskContext setRunURI(final String runURI) { this.runURI = runURI; return this; }
    @Override public AbstractTaskContext setCompleteURI(final String completeURI) { this.completeURI = completeURI; return this; }
    @Override public AbstractTaskContext setFingerPrint(final FingerPrint fingerPrint) { this.fingerPrint = fingerPrint; return this; }
    @Override public AbstractTaskContext setMsgTransporter(final Message<String> msgTransporter) { this.msgTransporter = msgTransporter; return this; }
}
