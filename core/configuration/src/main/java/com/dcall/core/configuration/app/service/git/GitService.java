package com.dcall.core.configuration.app.service.git;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.generic.entity.repository.GitRepository;
import com.dcall.core.configuration.generic.entity.user.User;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.submodule.SubmoduleStatus;
import org.eclipse.jgit.transport.FetchResult;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface GitService {
    Git initRepository(final File repo);
    GitRepository createRepository(final String path);
    GitRepository getRepository(final File fileRepo);
    GitRepository createSystemRepository(final RuntimeContext context);
    RevCommit commitSystemRepository(RuntimeContext context, GitRepository repo, String commitMsg);

    GitRepository createUserRepository(final User user);
    Git clone(final String src, final String dest);

    GitRepository fetchSubModules(final GitRepository repository);

    // git
    FetchResult fetchRepository(final Repository r);
    Git open(final File repo);
    Git close(final Git repo);

    Collection<String> initSubModule(final Git git);
    Repository addSubModule(final Git git, final String path, final String name);
    Map<String, SubmoduleStatus> subModuleStatus(Git git);
    Collection<String> subModuleUpdate(Git git);

    void addFilePath(final Git git, final String path);
    RevCommit commit(final Git git, final String msg);
    PullResult pull(Git git);

    Status status(final Git sysGit);
    Ref reset(final Git git, final ResetCommand.ResetType resetType, final String ref);
    Ref checkoutRepository(final Git git);

    // util
    String getGitPath(final String repo);
    String getCanonicalPath(final Git git);
    boolean isGitRepository(final String dirPath);
    String branchRefName(final String branch);
    String getRefHash(final GitRepository repo, final String name);

    String getSystemRepository();
}
