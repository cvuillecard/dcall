package com.dcall.core.configuration.generic.service.command;

import com.dcall.core.configuration.app.context.RuntimeContext;
import com.dcall.core.configuration.app.exception.FunctionalException;
import com.dcall.core.configuration.utils.FileUtils;
import com.dcall.core.configuration.utils.StringUtils;
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

    @Override
    public byte[] execute(final String... params) {
        try {
            LOG.debug("cmd params : " + params);
            if (this.context == null || this.helpFile == null) {
                throw new FunctionalException("Missing initialization of Abstract Command > AbstractCommand.init(final RuntimeContext context, final String helpFile) has not been called");
            }
            this.datas = this.run(params);
        }
        catch (FunctionalException e) {
            e.log();
            datas = e.getMessage().getBytes();
        }

        return this.datas;
    }

    public abstract byte[] run(final String... params);

    @Override public byte[] getDatas() { return datas; }
    @Override public RuntimeContext getContext() { return context; }
    @Override public String getHelp() { return helpFile; }
}
