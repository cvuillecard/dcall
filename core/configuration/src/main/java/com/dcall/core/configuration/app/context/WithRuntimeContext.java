package com.dcall.core.configuration.app.context;

import java.io.Serializable;

public interface WithRuntimeContext extends Serializable {
    <T> T setContext(final RuntimeContext context);
    RuntimeContext getContext();
}
