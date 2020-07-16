package com.dcall.core.configuration.app.exception;

public final class ExceptionHolder {
    private Exception exception;

    public ExceptionHolder throwException() throws Exception {
        if (exception != null)
            throw new Exception(exception);

        return this;
    }

    public Exception getException() { return exception; }
    public ExceptionHolder setException(final Exception exception) { this.exception = exception; return this; }
    public ExceptionHolder setException(final Throwable throwable) { this.exception = new Exception(throwable); return this; }

    public boolean hasException() { return exception != null; }
}
