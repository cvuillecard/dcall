package com.dcall.core.app.terminal.vertx.constant;

public final class URIConfig {
    public static final String BASE_URI_END_POINT = "com.dcall.core.app";
    public static final String CMD_PROCESSOR_DOMAIN = BASE_URI_END_POINT + ".processor.vertx.command";

    // end-points
    public static final String CMD_PROCESSOR_CONSUMER = CMD_PROCESSOR_DOMAIN + ".CommandProcessorConsumerVerticle";
    public static final String CMD_LOCAL_PROCESSOR_CONSUMER = CMD_PROCESSOR_DOMAIN + ".local.LocalCommandProcessorConsumerVerticle";
}
