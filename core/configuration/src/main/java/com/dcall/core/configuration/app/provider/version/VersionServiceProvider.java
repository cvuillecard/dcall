package com.dcall.core.configuration.app.provider.version;

import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;
import com.dcall.core.configuration.app.service.git.GitService;
import com.dcall.core.configuration.app.service.git.GitServiceImpl;

public final class VersionServiceProvider {
    private final EnvironService environService;
    private final GitService gitService;

    public VersionServiceProvider() {
        this.environService = new EnvironServiceImpl();
        this.gitService = new GitServiceImpl(this.environService);
    }

    public VersionServiceProvider(final EnvironService environService) {
        this.environService = environService;
        this.gitService = new GitServiceImpl(this.environService);
    }

    public EnvironService environService() { return environService; }
    public GitService gitService() { return gitService; }
}
