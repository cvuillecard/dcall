package com.dcall.core.configuration.app.context;

public interface WithRuntimeContext {
    <T> T setContext(final RuntimeContext context);
    RuntimeContext getContext();
}
