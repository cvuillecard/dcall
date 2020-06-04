package com.dcall.core.configuration.generic.entity.repository;

import com.dcall.core.configuration.generic.entity.Entity;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.submodule.SubmoduleStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class GitRepositoryBean implements GitRepository<String> {
    private static final Logger LOG  = LoggerFactory.getLogger(GitRepositoryBean.class);

    private String id;
    private Git git;
    private String path;
    private Map<String, SubmoduleStatus> subModules = new HashMap<>();

    public GitRepositoryBean() {}
    public GitRepositoryBean(final Git git) { initFromInstance(git); }

    @Override
    public GitRepository<String> initFromInstance(final Git git) {
        try {
            if (git != null) {
                this.git = git;
                this.path = this.git.getRepository().getDirectory().getAbsolutePath();
                this.subModules = this.git.submoduleStatus().call();
            }
        }
        catch (GitAPIException e) {
            LOG.error(e.getMessage());
        }
        finally {
            return this;
        }
    }

    @Override
    public SubmoduleStatus submoduleStatus(final String relativePath) {
        if (!this.subModules.isEmpty() && subModules.get(relativePath) != null)
            return subModules.get(relativePath);

        return null;
    }

    // getters
    @Override public String getId() { return this.id; }
    @Override public Git getGit() { return git; }
    @Override public String getPath() { return path; }
    @Override public Map<String, SubmoduleStatus> getSubModules() { return subModules; }

    // setters
    @Override public Entity<String> setId(final String id) { this.id = id; return this; }
    @Override public GitRepository<String> setGit(final Git git) { this.git = git; return this; }
    @Override public GitRepository<String> setPath(final String path) { this.path = path; return this; }
    @Override public GitRepository<String> setSubModules(final Map<String, SubmoduleStatus> subModules) { this.subModules = subModules; return this; }
}
