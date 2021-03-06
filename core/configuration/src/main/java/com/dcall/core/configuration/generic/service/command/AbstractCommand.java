package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.constant.GitMessage;
import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.exception.FunctionalException;
import com.dcall.core.configuration.app.service.git.GitService;
import com.dcall.core.configuration.app.entity.repository.GitRepository;
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
    protected RuntimeContext runtimeContext;
    private String helpFile;
    private byte[] datas;
    private String[] params;

    @Override
    public GenericCommandService init(final RuntimeContext runtimeContext, final String helpFile) {
        this.runtimeContext = runtimeContext;
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
        return GitMessage.getLocalSnapshotUserMsg(runtimeContext.userContext().getUser(), msg);
    }

    @Override
    public RevCommit commit(final String msg) {
        if (runtimeContext != null) {
            final GitRepository repository = getRuntimeContext().systemContext().versionContext().getRepository();
            final GitService gitService = getRuntimeContext().serviceContext().serviceProvider().versionServiceProvider().gitService();

            if (gitService.isAutoCommit(runtimeContext) && repository != null) {
                LOG.debug("built-in : command commit with message : " + msg);
                return gitService.commitSystemRepository(getRuntimeContext(), repository, commitMessage(msg));
            }
            else
                LOG.debug("built-in : cannot commit, env.auto_commit = false");
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
            if (this.runtimeContext == null || this.helpFile == null) {
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
    @Override public RuntimeContext getRuntimeContext() { return this.runtimeContext; }
    @Override public String getHelp() { return this.helpFile; }
    @Override public String[] getParams() { return params; }
    @Override public byte[] getDatas() { return this.datas; }

    // setter
    @Override public GenericCommandService setRuntimeContext(final RuntimeContext runtimeContext) { this.runtimeContext = runtimeContext; return this; }
    @Override public GenericCommandService setHelp(final String helpFile) { this.helpFile = helpFile; return this; }
    @Override public GenericCommandService setParams(String[] params) { this.params = params; return this; }
    @Override public GenericCommandService setDatas(byte[] datas) { this.datas = datas; return this; }
}
