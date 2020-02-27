package com.dcall.core.configuration.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TechnicalException extends Exception {
    private static final Logger LOG = LoggerFactory.getLogger(TechnicalException.class);

    public TechnicalException() { super(); }
    public TechnicalException(final String message, final Throwable cause) { super(message, cause); }
    public TechnicalException(final String message) { super(message); }
    public TechnicalException(final Throwable cause) { super(cause); }

    public void log() {
        LOG.error(this.getClass().getName() + " : " + this.getMessage() + " - " + this.getCause());
    }
}
