package com.dcall.core.app.client.terminal.vertx.constant;

public final class URIConfig {
    public static final String BASE_URI_END_POINT = "com.dcall.core.app.server";
    public static final String CMD_PROCESSOR_DOMAIN = BASE_URI_END_POINT + ".processor.vertx.command";
    public static final String CMD_PROCESSOR_CONSUMER = CMD_PROCESSOR_DOMAIN + ".CommandProcessorConsumerVerticle";
}
