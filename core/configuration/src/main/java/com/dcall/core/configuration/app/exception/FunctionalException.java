package com.dcall.core.configuration.app.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FunctionalException extends Exception {
    private static final Logger LOG = LoggerFactory.getLogger(FunctionalException.class);

    public FunctionalException() {
        super();
    }
    public FunctionalException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public FunctionalException(final String message) {
        super(message);
    }
    public FunctionalException(final Throwable cause) {
        super(cause);
    }

    public void log() {
        LOG.error(this.getClass().getName() + " : " + this.getMessage() + " - " + this.getCause());
    }
}
