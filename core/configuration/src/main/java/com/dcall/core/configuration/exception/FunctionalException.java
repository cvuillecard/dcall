package com.dcall.core.configuration.exception;

public class FunctionalException extends Exception {

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
}
