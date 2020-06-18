package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.constant.EnvironConstant;
import com.dcall.core.configuration.app.constant.GitCommitMode;
import com.dcall.core.configuration.app.constant.GitMessage;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.exception.FunctionalException;
import com.dcall.core.configuration.app.service.git.GitService;
import com.dcall.core.configuration.generic.entity.repository.GitRepository;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.StringUtils;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractCommand implements GenericCommandService {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCommand.class);
    private RuntimeContext context;
    private String helpFile;
    private byte[] datas;
    private String[] params;

    @Override
    public GenericCommandService init(final RuntimeContext context, final String helpFile) {
        this.context = context;
        this.helpFile = helpFile;

        return this;
    }

    @Override
    public byte[] usage() {
        try {
            Objects.requireNonNull(this.helpFile);
            this.datas = FileUtils.getInstance().readAllBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream(this.helpFile));
        } catch (IOException e) {
            return handleException(e);
        }

        return this.datas;
    }

    @Override
    public String commitMessage(final String msg) {
        return GitMessage.getLocalSnapshotUserMsg(context.userContext().getUser(), msg);
    }

    @Override
    public RevCommit commit(final String msg) {
        if (context != null) {
            final GitRepository repository = getContext().systemContext().getRepository();
            final boolean auto_commit = Boolean.valueOf(context.userContext().getEnviron().getEnv().get(EnvironConstant.COMMIT_MODE).toString());
            final GitService gitService = getContext().serviceContext().serviceProvider().versionServiceProvider().gitService();

            if (GitCommitMode.AUTO.mode() == auto_commit && repository != null)
                return gitService.commitSystemRepository(getContext(), repository, commitMessage(msg));
        }

        return null;
    }

    /**
     * Method called by the subclass implementing the functional code of a command with arguments or not.
     *
     * If params is null or empty, then abstract execute() is called in subclass, otherwise the method execute(final String... params)
     * is called and datas is returned as the result of the command.
     *
     * Note : exception's messages are returned as datas
     *
     * @param params
     * @return byte[] datas -> result of 'execute' methods (each command erase last value)
     */
    @Override
    public byte[] run(final String... params) {
        try {
            if (params != null)
                LOG.debug("cmd params : " + StringUtils.listToString(Arrays.asList(params)));
            if (this.context == null || this.helpFile == null) {
                throw new FunctionalException("Missing initialization of Abstract Command > AbstractCommand.init(final RuntimeContext context, final String helpFile) has not been called");
            }
            this.datas = params != null && params.length > 0 ? this.execute(params) : this.execute();
        }
        catch (Exception e) {
            return handleException(e);
        }

        return this.datas;
    }

    @Override
    public byte[] run() {
        return this.run(this.params);
    }

    @Override
    public byte[] handleException(final Exception e) {
        final String msg = e.getMessage() != null ? e.getMessage() : e.toString();
        LOG.debug(msg);
        this.datas = msg.getBytes();

        return datas;
    }

    public abstract byte[] execute(final String... params) throws Exception;
    public abstract byte[] execute() throws Exception;

    // getter
    @Override public RuntimeContext getContext() { return this.context; }
    @Override public String getHelp() { return this.helpFile; }
    @Override public String[] getParams() { return params; }
    @Override public byte[] getDatas() { return this.datas; }

    // setter
    @Override public GenericCommandService setContext(final RuntimeContext context) { this.context = context; return this; }
    @Override public GenericCommandService setHelp(final String helpFile) { this.helpFile = helpFile; return this; }
    @Override public GenericCommandService setParams(String[] params) { this.params = params; return this; }
    @Override public GenericCommandService setDatas(byte[] datas) { this.datas = datas; return this; }
}
