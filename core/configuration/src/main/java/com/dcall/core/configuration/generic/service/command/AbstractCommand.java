package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.exception.FunctionalException;
import com.dcall.core.configuration.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
            Objects.requireNonNull(helpFile);
            datas = FileUtils.getInstance().readAllBytes(Thread.currentThread().getContextClassLoader().getResourceAsStream(helpFile));
        } catch (IOException e) {
            datas = e.getMessage().getBytes();
            LOG.error(e.getMessage());
        }

        return datas;
    }

    /**
     * Method called by the subclass implementing the functional code of a command with arguments or not.
     *
     * If params is null or empty, then abstract execute() is called in subclass, otherwise the method execute(final String... params)
     * is called and datas is returned as the result of the command.
     *
     * Note : errors should be returned by the 'execute' methods implemented in the subclass to be used later (for printing or other)
     *
     * @param params
     * @return byte[] datas -> result of 'execute' methods (each command erase last value)
     */
    @Override
    public byte[] run(final String... params) {
        try {
            LOG.debug("cmd params : " + params);
            if (this.context == null || this.helpFile == null) {
                throw new FunctionalException("Missing initialization of Abstract Command > AbstractCommand.init(final RuntimeContext context, final String helpFile) has not been called");
            }
            this.datas = params != null && params.length > 0 ? this.execute(params) : this.execute();
        }
        catch (FunctionalException e) {
            e.log();
            datas = e.getMessage().getBytes();
        }

        return this.datas;
    }

    public abstract byte[] execute(final String... params);
    public abstract byte[] execute();

    @Override public byte[] getDatas() { return datas; }
    @Override public RuntimeContext getContext() { return context; }
    @Override public String getHelp() { return helpFile; }
}
