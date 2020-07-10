package com.dcall.core.configuration.generic.cluster.vertx.ssl;

import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.net.JksOptions;

public final class VertxEventBusSSLConfigurator
{
    private VertxOptions vertxOptions = new VertxOptions();
    private EventBusOptions eventBusOptions = new EventBusOptions();
    private String keyStoreFilePath = null;
    private String trustStoreFilePath = null;
    private String pwdKeyStore = null;
    private String pwdTrustStore = null;

    public VertxEventBusSSLConfigurator() {}

    public VertxEventBusSSLConfigurator(final VertxOptions vertxOptions, final EventBusOptions eventBusOptions) {
        this.vertxOptions = vertxOptions;
        this.eventBusOptions = eventBusOptions;
    }

    public final VertxOptions execute()
    {
        setEventBusSSL(true).setEventBusKeyStore(keyStoreFilePath, pwdKeyStore)
                .setEventBusTrustStore(trustStoreFilePath, pwdTrustStore);
        return vertxOptions.setEventBusOptions(eventBusOptions.setClientAuth(ClientAuth.REQUIRED));
    }

    private VertxEventBusSSLConfigurator setEventBusSSL(final boolean state) {
        eventBusOptions.setSsl(state);
        return this;
    }

    private VertxEventBusSSLConfigurator setEventBusKeyStore(final String jksKeyStoreFileName, final String password) {
        eventBusOptions.setKeyStoreOptions(new JksOptions()
                .setPath(keyStoreFilePath).setPassword(pwdKeyStore));
        return this;
    }

    private VertxEventBusSSLConfigurator setEventBusTrustStore(final String jksKeyTrustFileName, final String password) {
        eventBusOptions.setTrustStoreOptions(new JksOptions()
                .setPath(keyStoreFilePath).setPassword(pwdKeyStore));
        return this;
    }

    // GETTERS
    public final VertxOptions getVertxOptions() { return vertxOptions; }
    public final String getKeyStoreFilePath() { return this.keyStoreFilePath; }
    public final String getTrustStoreFilePath() { return this.trustStoreFilePath; }

    // SETTERS
    public VertxEventBusSSLConfigurator setVertxOptions(VertxOptions vertxOptions) {
        this.vertxOptions = vertxOptions;
        return this;
    }

    public VertxEventBusSSLConfigurator setKeyStoreFilePath(final String filePath) {
        this.keyStoreFilePath = filePath;
        return this;
    }

    public VertxEventBusSSLConfigurator setTrustStoreFilePath(final String filePath) {
        this.trustStoreFilePath= filePath;
        return this;
    }

    public VertxEventBusSSLConfigurator setPwdKeyStore(final String pwdKeyStore) {
        this.pwdKeyStore = pwdKeyStore;
        return this;
    }

    public VertxEventBusSSLConfigurator setPwdTrustStore(final String pwdTrustStore) {
        this.pwdTrustStore = pwdTrustStore;
        return this;
    }
}
