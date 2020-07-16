package com.dcall.core.configuration.utils;

import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.eventbus.ReplyFailure;

public final class VertxUtils {

    public static ReplyException replyException(final Throwable cause, final String message) {
        final ReplyException e = new ReplyException(ReplyFailure.RECIPIENT_FAILURE, -1, message);

        e.initCause(cause);

        return e;
    }

    public static ReplyException replyException(final Throwable cause, final ReplyFailure replyFailure, final String message) {
        final ReplyException e = new ReplyException(replyFailure, -1, message);

        e.initCause(cause);

        return e;
    }
}
