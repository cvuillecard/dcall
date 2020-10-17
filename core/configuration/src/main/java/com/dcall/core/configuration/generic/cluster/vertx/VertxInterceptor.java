package com.dcall.core.configuration.generic.cluster.vertx;

import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VertxInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(VertxInterceptor.class);
    private static boolean log = false;

    public static void logRequest(final Vertx vertx) {
        if (!log) {
            vertx.eventBus().addInboundInterceptor(ctx -> {
                if (ctx.message().body() != null)
                    LOG.debug("RECV > [" + ctx.message().address() + "] - body : " + ctx.message().body().toString());
                ctx.next();
            });

            vertx.eventBus().addOutboundInterceptor(ctx -> {
                if (ctx.body() != null)
                    LOG.debug("SEND > [" + ctx.message().address() + "] - body : " + ctx.body().toString());
                ctx.next();
            });
            log = true;
        }
    }
}
