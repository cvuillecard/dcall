package com.dcall.core.configuration.app.service.git;

import com.dcall.core.configuration.app.context.user.UserContext;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Repository;

import java.io.File;

public interface GitService {
    Git initRepository(final File repository);
    Repository checkoutRepository(final Git git);
    String getGitPath(final String repo);
    GitService createSystemRepository(final UserContext context);
    void addFilePath(final Git git, final String path);
    void commit(final Git git, final String msg);
    Status status(final Git sysGit);
    Git openGitFile(final File fGit);
    String getSystemRepository();
}
