package com.dcall.core.configuration.vertx;

public class CrudUriConfig {
    private final String _CREATE = "/POST";
    private final String _READ = "/GET";
    private final String _UPDATE = "/PUT";
    private final String _DELETE = "/DELETE";
    private String uri;

    private CrudUriConfig() {}
    public CrudUriConfig(final String uri) { this.uri = uri; }

    public String create() { return uri + _CREATE; }
    public String read() { return uri + _READ; }
    public String update() { return uri + _UPDATE; }
    public String delete() { return uri + _DELETE; }
}
