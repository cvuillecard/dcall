package com.dcall.core.configuration.exception;

public class TechnicalException extends Exception {

    public TechnicalException() { super(); }
    public TechnicalException(final String message, final Throwable cause) { super(message, cause); }
    public TechnicalException(final String message) { super(message); }
    public TechnicalException(final Throwable cause) { super(cause); }
}
