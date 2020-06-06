package com.dcall.core.app.processor.bean.local.service.command;

import com.dcall.core.configuration.generic.service.command.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalHelpCommandServiceImpl extends AbstractCommand implements LocalHelpCommandService {
    private static final Logger LOG = LoggerFactory.getLogger(LocalHelpCommandServiceImpl.class);

    @Override
    public byte[] execute(final String... params) {
        return this.usage();
    }

    @Override
    public byte[] execute() {
        return this.usage();
    }
}
