package com.dcall.core.configuration.generic.entity.repository;

import com.dcall.core.configuration.generic.entity.Entity;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.submodule.SubmoduleStatus;

import java.util.Map;

public interface GitRepository<ID> extends Entity<ID> {

    // getters
    Git getGit();
    String getPath();
    Map<String, SubmoduleStatus> getSubModules();

    // setters
    GitRepository<String> setGit(final Git git);
    GitRepository<String> setPath(final String path);
    GitRepository<String> setSubModules(final Map<String, SubmoduleStatus> subModules);

    // util
    GitRepository<String> initFromInstance(final Git git);
    SubmoduleStatus submoduleStatus(final String relativePath);
}
