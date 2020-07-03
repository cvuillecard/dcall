package com.dcall.core.configuration.app.context.vertx.uri;

import com.dcall.core.configuration.generic.vertx.uri.VertxURIConfig;

public final class VertxURIContext {
    private String baseLocalAppUri = VertxURIConfig.BASE_APP_URI;
    private String baseRemoteAppUri;
    private String localConsumerUri;
    private String remoteConsumerUri;

    // getters
    public String getBaseLocalAppUri() { return baseLocalAppUri; }
    public String getBaseRemoteAppUri() { return baseRemoteAppUri; }
    public String getLocalConsumerUri() { return localConsumerUri; }
    public String getRemoteConsumerUri() { return remoteConsumerUri; }

    // setters

    public VertxURIContext setBaseLocalAppUri(final String baseLocalAppUri) { this.baseLocalAppUri = baseLocalAppUri; return this; }
    public VertxURIContext setBaseRemoteAppUri(final String baseRemoteAppUri) { this.baseRemoteAppUri = baseRemoteAppUri; return this; }
    public VertxURIContext setLocalConsumerUri(final String localConsumerUri) { this.localConsumerUri = localConsumerUri; return this; }
    public VertxURIContext setRemoteConsumerUri(final String remoteConsumerUri) { this.remoteConsumerUri = remoteConsumerUri; return this; }

    // utils
    public String getLocalUri(final String subUri) { return baseLocalAppUri + '.' + subUri; }
    public String getRemoteUri(final String subUri) { return baseRemoteAppUri + '.' + subUri; }
}
