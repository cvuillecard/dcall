package com.dcall.core.configuration.app.service.git;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.GitConstant;
import com.dcall.core.configuration.app.context.user.UserContext;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;
import com.dcall.core.configuration.utils.ResourceUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GitServiceImpl implements GitService {
    private static final Logger LOG = LoggerFactory.getLogger(GitServiceImpl.class);
    private final EnvironService environService;

    public GitServiceImpl() { this.environService = new EnvironServiceImpl(); }
    public GitServiceImpl(final EnvironService environService) { this.environService = environService; }

    @Override
    public String getGitPath(String repo) {
        return repo + File.separator + GitConstant.GIT_FILENAME;
    }

    @Override
    public GitService createSystemRepository(final UserContext context) {
        final String sysPath = getSystemRepository();
        final File rFile = new File(sysPath);
        final File sFile = new File(getGitPath(sysPath));
        final File uFile = new File(getGitPath(context.getUser().getPath()));
        Git sysGit;
        Git userGit;

        if (rFile.exists()) {
            if (sFile.exists()) {
                checkoutRepository(sysGit = openGitFile(sFile));
                checkoutRepository(userGit = openGitFile(uFile));
            }
            else {
                final String[] confPath = environService.getConfigDirectory().split(File.separator);
                final String[] workspace = context.getUser().getPath().split(File.separator);
                final String[] userHome = context.getEnviron().getEnv().get(EnvironConstant.USER_HOME).toString().split(File.separator);
                sysGit = initRepository(rFile);

                userGit = initRepository(new File(context.getUser().getPath()));
                addFilePath(userGit, userHome[userHome.length - 1]);
                commit(userGit, context.getUser().getLogin() + " - init user workspace [" + context.getUser().getPath() + "]");

                addFilePath(sysGit, confPath[confPath.length - 1]);
                addFilePath(sysGit, workspace[workspace.length - 1]);

                commit(sysGit, "Dcall init configuration");
//                status(sysGit);
//                status(userGit);
            }
        }

        return this;
    }

    @Override
    public void addFilePath(final Git git, final String path) {
        try {
            git.add().addFilepattern(path).call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public void commit(final Git git, final String msg) {
        try {
            git.commit().setMessage(msg).call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public Status status(final Git sysGit) {
        try {
            return sysGit.status().call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Git initRepository(final File repository) {
        try {
            return Git.init().setDirectory(repository).call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Repository checkoutRepository(Git git) {
        return git.checkout().getRepository();
    }

    @Override
    public Git openGitFile(final File fGit) {
        try {
            return Git.open(fGit);
        }
        catch (IOException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public String getSystemRepository() {
        return ResourceUtils.localProperties().getProperty(GitConstant.SYS_GIT_REPOSITORY);
    }
}
