package com.dcall.core.configuration.context.cluster;

public class ClusterInfo {
    private String domain = "gate";
    private String group = "public";
    private boolean connected = false;

    public String getDomain() { return domain; }
    public String getGroup() { return group; }

    public void setDomain(String domain) { this.domain = domain;    }
    public void setGroup(String group) { this.group = group; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public boolean isConnected() { return connected; }
}
