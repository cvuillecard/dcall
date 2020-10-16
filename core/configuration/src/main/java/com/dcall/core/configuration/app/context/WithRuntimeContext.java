package com.dcall.core.configuration.app.context;

import java.io.Serializable;

public interface WithRuntimeContext extends Serializable {
    <T> T setRuntimeContext(final RuntimeContext runtimeContext);
    RuntimeContext getRuntimeContext();
}
