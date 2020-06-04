package com.dcall.core.configuration.app.service.git;

import com.dcall.core.configuration.app.constant.GitConstant;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.exception.TechnicalException;
import com.dcall.core.configuration.app.service.environ.EnvironService;
import com.dcall.core.configuration.app.service.environ.EnvironServiceImpl;
import com.dcall.core.configuration.generic.entity.repository.GitRepository;
import com.dcall.core.configuration.generic.entity.repository.GitRepositoryBean;
import com.dcall.core.configuration.generic.entity.user.User;
import com.dcall.core.configuration.utils.ResourceUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.submodule.SubmoduleStatus;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class GitServiceImpl implements GitService {
    private static final Logger LOG = LoggerFactory.getLogger(GitServiceImpl.class);
    private final EnvironService environService;
    private boolean initSubModules;

    public GitServiceImpl() { this.environService = new EnvironServiceImpl(); }
    public GitServiceImpl(final EnvironService environService) { this.environService = environService; }

    @Override
    public Git initRepository(final File repo) {
        try {
            return Git.init().setDirectory(repo).call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public GitRepository createRepository(final String path) {
        final File repo = new File(path);

        try {
            if (repo.exists())
                return isGitRepository(path) ? getRepository(repo) : new GitRepositoryBean(initRepository(repo));
            else throw new TechnicalException(path + " doesn't exists");
        }
        catch (TechnicalException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public GitRepository getRepository(final File repo) {
        try {
            if (repo.exists() && isGitRepository(repo.getAbsolutePath()))
                return new GitRepositoryBean(open(repo));
            else throw new TechnicalException(repo.getAbsolutePath() + " is not a repository or doesn't exists");
        }
        catch (TechnicalException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public GitRepository createSystemRepository(final RuntimeContext context) {
        GitRepository repo = null;
        final String sysPath = getSystemRepository();
        final File gitFile = new File(getGitPath(sysPath));
        final boolean isUserSubDir = context.userContext().getUser().getPath().contains(sysPath);
        final String[] userWorkspace = context.userContext().getUser().getPath().split(File.separator);

        if (!gitFile.exists()) {
            repo = createRepository(sysPath);

            addFilePath(repo.getGit(), context.serviceContext().serviceProvider().environService().getConfigDirName());

            if (isUserSubDir)
                addFilePath(repo.getGit(), userWorkspace[userWorkspace.length - 1]);
            else {
                final GitRepository userRepo = createUserRepository(context.userContext().getUser());
                addSubModule(repo.getGit(), getCanonicalPath(userRepo.getGit()), userWorkspace[userWorkspace.length - 1]).close();
            }

            commit(repo.getGit(), "DCall init configuration");
            repo.initFromInstance(repo.getGit());
        }
        else {
            reset((repo = getRepository(new File(sysPath))).getGit(), ResetCommand.ResetType.HARD, GitConstant.MASTER);
//            if (!isUserSubDir) {
//                pull(repo.getGit());
////                initSubModule(repo.getGit());
////                subModuleUpdate(repo.getGit());
//                clone(repo.getGit().getRepository().getDirectory().getParent(), repo.getPath() + File.separator + userWorkspace[userWorkspace.length - 1]);
////                fetchSubModules(repo);
//            }
        }

        return repo;
    }

    @Override
    public GitRepository createUserRepository(final User user) {
        final File workspace = new File(user.getPath());

        if (workspace.exists()) {
            final GitRepository repository  = createRepository(user.getPath());
            Arrays.stream(workspace.listFiles()).forEach(f -> addFilePath(repository.getGit(), f.getName()));

            commit(repository.getGit(), user.getLogin() + " repository creation.");

            return repository.initFromInstance(repository.getGit());
        }

        return null;
    }

    @Override
    public Git clone(final String src, final String dest) {
        try {
            return Git.cloneRepository().setURI(getGitPath(src)).setDirectory(new File(dest))
                    .setCloneAllBranches(true)
                    .setBranchesToClone(Arrays.asList(getBranch(GitConstant.MASTER)))
                    .setBranch(getBranch(null))
                    .call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    @Override
    public GitRepository fetchSubModules(final GitRepository repository) {
        try {
            final SubmoduleWalk subModules = SubmoduleWalk.forIndex(repository.getGit().getRepository());
            while (subModules.next()) {
                final Repository sub = subModules.getRepository();
                fetchRepository(sub);
                sub.close();
//                final Git git = open(sub.getDirectory());
//                reset(git, ResetCommand.ResetType.HARD, GitConstant.MASTER);
//                git.close();
            }
        }
        catch (IOException e) {
            LOG.error(e.getMessage());
        }
        finally {
            return repository;
        }
    }

    @Override
    public FetchResult fetchRepository(final Repository r) {
        try {
            return Git.wrap(r).fetch().call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Git open(final File repo) {
        try {
            return Git.open(repo);
        }
        catch (IOException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Git close(final Git repo) {
        repo.close();
        return repo;
    }

    @Override
    public Collection<String> initSubModule(final Git git) {
        try {
            return git.submoduleInit().call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Repository addSubModule(final Git git, final String path, final String relativePath) {
        try {
            return git.submoduleAdd().setURI(path).setPath(relativePath).call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Map<String, SubmoduleStatus> subModuleStatus(final Git git) {
        try {
            return git.submoduleStatus().call();
        } catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Collection<String> subModuleUpdate(final Git git) {
        try {
            return git.submoduleUpdate().call();
        } catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    @Override
    public void addFilePath(final Git git, final String relativePath) {
        try {
            git.add().addFilepattern(relativePath).call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public RevCommit commit(final Git git, final String msg) {
        try {
            return git.commit().setMessage(msg).call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public PullResult pull(final Git git) {
        try {
            return git.pull().call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    @Override
    public Status status(final Git git) {
        try {
            return git.status().call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Ref reset(final Git git, final ResetCommand.ResetType resetType, final String ref) {
        try {
            if (resetType != null) {
                if (ref != null && !ref.isEmpty())
                    return git.reset().setMode(resetType).setRef(GitConstant.BRANCH_REFS + ref).call();
                else
                    return git.reset().setMode(resetType).call();
            }
            else
                return git.reset().call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public Ref checkoutRepository(Git git) {
        try {
            return git.checkout().call();
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public String getGitPath(final String repo) {
        return repo + File.separator + GitConstant.GIT_FILENAME;
    }

    @Override
    public String getCanonicalPath(final Git git) {
        try {
            return git.getRepository().getDirectory().getCanonicalPath();
        }
        catch (IOException e) {
            LOG.error(e.getMessage());
        }

        return null;
    }

    @Override
    public boolean isGitRepository(final String dirPath) {
        if (dirPath != null && !dirPath.isEmpty()) {
            return new File(getGitPath(dirPath)).exists();
        }

        return false;
    }

    @Override
    public String getBranch(final String branch) {
        if (branch != null && !branch.isEmpty())
            return GitConstant.BRANCH_REFS  + branch;
        return GitConstant.BRANCH_REFS + GitConstant.MASTER;
    }

    @Override
    public String getSystemRepository() {
        return ResourceUtils.localProperties().getProperty(GitConstant.SYS_GIT_REPOSITORY);
    }
}
