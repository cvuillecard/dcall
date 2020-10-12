package com.dcall.core.configuration.app.context.version;

import com.dcall.core.configuration.app.entity.repository.GitRepository;

import java.io.Serializable;

public final class VersionContext implements Serializable {
    private GitRepository repository = null;

    public VersionContext setRepository(final GitRepository repository) { this.repository = repository; return this; }
    public GitRepository getRepository() { return this.repository; }
}
