package com.dcall.core.configuration.app.context.system;

import com.dcall.core.configuration.generic.entity.repository.GitRepository;

public final class SystemContext {
    GitRepository repository = null;

    public SystemContext setRepository(final GitRepository repository) { this.repository = repository; return this; }
    public GitRepository getRepository() { return this.repository; }
}
