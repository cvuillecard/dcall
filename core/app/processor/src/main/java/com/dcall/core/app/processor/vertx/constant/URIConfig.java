package com.dcall.core.app.processor.vertx.constant;

public final class URIConfig {
    public static final String BASE_URI_END_POINT = "com.dcall.core.app";

    // CLI
    public static final String URI_CLIENT_CLI_DOMAIN = BASE_URI_END_POINT + ".cli.vertx";
    public static final String URI_CLIENT_CLI_CONSUMER = URI_CLIENT_CLI_DOMAIN + ".InputConsumerVerticle";

    // TERMINAL
    public static final String URI_CLIENT_TERMINAL_DOMAIN = BASE_URI_END_POINT + ".terminal.vertx";
    public static final String URI_CLIENT_TERMINAL_CONSUMER = URI_CLIENT_TERMINAL_DOMAIN + ".InputConsumerVerticle";
}
