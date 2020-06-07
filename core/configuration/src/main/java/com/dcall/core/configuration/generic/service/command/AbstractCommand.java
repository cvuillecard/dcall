package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.exception.FunctionalException;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.StringUtils;
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

    @Override
    public AbstractCommand init(final RuntimeContext context, final String helpFile) {
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
    public byte[] handleException(final Exception e) {
        if (this.datas == null) {
            final String msg = e.getMessage() != null ? e.getMessage() : e.toString();
            LOG.debug(msg);
            this.datas = msg.getBytes();
        }

        return datas;
    }

    public abstract byte[] execute(final String... params) throws Exception;
    public abstract byte[] execute() throws Exception;

    @Override public byte[] getDatas() { return this.datas; }
    @Override public RuntimeContext getContext() { return this.context; }
    @Override public String getHelp() { return this.helpFile; }
}
