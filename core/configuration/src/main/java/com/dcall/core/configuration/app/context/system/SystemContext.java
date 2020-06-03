package com.dcall.core.configuration.app.context.system;

public final class SystemContext {
    private boolean gitInit = false; // temporaire car pas tres joli //todo RepositoryBean

    public SystemContext setGitInit(final boolean state) { this.gitInit = state; return this; }
    public boolean isGitInit() { return this.gitInit; }
}
